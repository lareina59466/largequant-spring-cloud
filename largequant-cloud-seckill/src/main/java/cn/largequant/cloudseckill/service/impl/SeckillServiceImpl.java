package cn.largequant.cloudseckill.service.impl;

import cn.largequant.cloudcommon.util.RedisUtil;
import cn.largequant.cloudcommon.constant.seckill.RedisConstants;
import cn.largequant.cloudseckill.dao.SeckillDao;
import cn.largequant.cloudseckill.dao.SuccessKilledDao;
import cn.largequant.cloudcommon.dto.seckill.Exposer;
import cn.largequant.cloudseckill.aop.Servicelock;
import cn.largequant.cloudcommon.entity.seckill.Seckill;
import cn.largequant.cloudcommon.entity.seckill.SuccessKilled;
import cn.largequant.cloudseckill.lock.redis.RedLock;
import cn.largequant.cloudseckill.service.SeckillService;
import cn.largequant.cloudseckill.lock.zk.ZkLock;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SeckillServiceImpl implements SeckillService {

    private SeckillDao seckillDao;
    private SuccessKilledDao successKilledDao;
    private RedisUtil redisUtil;

    @Autowired
    public SeckillServiceImpl(SeckillDao seckillDao, SuccessKilledDao successKilledDao, RedisUtil redisUtil) {
        this.seckillDao = seckillDao;
        this.successKilledDao = successKilledDao;
        this.redisUtil = redisUtil;
    }

    //查询所有秒杀记录
    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.getSeckillList(0, 5);
    }

    //查询单个秒杀记录
    @Override
    public Seckill getSeckillById(long seckillId) {
        return seckillDao.getSeckillById(seckillId);
    }

    //查询秒杀售卖商品
    @Override
    public Long getSeckillCount(long seckillId) {
        return successKilledDao.getNumberById(seckillId);
    }

    //暴露秒杀接口
    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        // 将秒杀信息放入redis中， 减少数据库压力
        String seckillStr = (String) redisUtil.getString(RedisConstants.SECKILL_PREFIX + seckillId);
        Seckill seckill = JSON.parseObject(seckillStr, Seckill.class);
        if (seckill == null) {
            // 从数据库中获取
            seckill = seckillDao.getSeckillById(seckillId);
            if (seckill == null) {
                Exposer exposer = new Exposer();
                exposer.setExposed(false);
                exposer.setSeckillId(seckillId);
                return exposer;
            } else {
                // 将秒杀信息放入redis中
                redisUtil.setString(RedisConstants.SECKILL_PREFIX + seckill.getId(), JSON.toJSONString(seckill), RedisConstants.SECKILL_EXPIRE_TIME);
            }
        }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();

        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            Exposer exposer = new Exposer();
            exposer.setExposed(false);
            exposer.setNow(nowTime.getTime());
            exposer.setStart(startTime.getTime());
            exposer.setEnd(endTime.getTime());
            exposer.setSeckillId(seckillId);
            return exposer;
        }

        String md5 = getMd5(seckillId);
        Exposer exposer = new Exposer();
        exposer.setExposed(true);
        exposer.setMd5(md5);
        exposer.setSeckillId(seckillId);
        return exposer;
    }

    //单体架构:程序锁
    @Override
    @Servicelock
    @Transactional(rollbackFor = Exception.class)
    public SuccessKilled executeByAopLock(long seckillId, long userId, String md5) {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            throw new RuntimeException("秒杀失败，涉嫌数据被修改");
        }
        //查询购买行为
        int insertCount = successKilledDao.getState(seckillId, userId);
        if (insertCount >= 0) {
            throw new RuntimeException("重复秒杀");
        } else {
            //查库存
            Long number = seckillDao.getNumberById(seckillId);
            //库存足够
            if (number > 0) {
                //更新商品库存就回滚事务
                if (seckillDao.updateNumberDecrement(seckillId, new Date()) == 0) {
                    throw new RuntimeException("更新商品库存失败");
                }
                //新增秒杀记录
                SuccessKilled successKilled = new SuccessKilled();
                successKilled.setSeckillId(seckillId);
                successKilled.setUserId(userId);
                successKilled.setState((short) 1);
                successKilled.setCreateTime(new Timestamp(new Date().getTime()));
                //插入失败就回滚事务
                if (successKilledDao.save(successKilled) == 0) {
                    throw new RuntimeException("新增秒杀记录失败");
                }
                //返回秒杀记录
                return successKilled;
            } else {
                //库存不足
                throw new RuntimeException("库存不足");
            }
        }
    }

    //单体架构:数据库乐观锁
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SuccessKilled executeByOptimisticLock(long seckillId, long userId, String md5) {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            throw new RuntimeException("秒杀失败，涉嫌数据被修改");
        }
        //查询购买行为
        int insertCount = successKilledDao.getState(seckillId, userId);
        if (insertCount >= 0) {
            throw new RuntimeException("重复秒杀");
        } else {
            //查库存
            Seckill seckill = seckillDao.getSeckillById(seckillId);
            //库存足够
            if (seckill.getNumber() > 0) {
                //更新商品库存就回滚事务
                if (seckillDao.updateNumberDecrementByVersion(seckillId, seckill.getVersion(), new Date()) == 0) {
                    throw new RuntimeException("更新商品库存失败");
                }
                //新增秒杀记录
                SuccessKilled successKilled = new SuccessKilled();
                successKilled.setSeckillId(seckillId);
                successKilled.setUserId(userId);
                successKilled.setState((short) 1);
                successKilled.setCreateTime(new Timestamp(new Date().getTime()));
                //插入失败就回滚事务
                if (successKilledDao.save(successKilled) == 0) {
                    throw new RuntimeException("新增秒杀记录失败");
                }
                return successKilled;
            } else {
                //库存不足
                throw new RuntimeException("库存不足");
            }
        }
    }

    //单体架构:数据库悲观锁
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SuccessKilled executeByPessimisticLock(long seckillId, long userId, String md5) {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            throw new RuntimeException("秒杀失败，涉嫌数据被修改");
        }
        //查询购买行为
        int insertCount = successKilledDao.getState(seckillId, userId);
        if (insertCount >= 0) {
            throw new RuntimeException("重复秒杀");
        } else {
            //查库存
            Long number = seckillDao.getNumberByIdForUpdate(seckillId);
            //库存足够
            if (number > 0) {
                //更新商品库存就回滚事务
                if (seckillDao.updateNumberDecrement(seckillId, new Date()) == 0) {
                    throw new RuntimeException("更新商品库存失败");
                }
                //新增秒杀记录
                SuccessKilled successKilled = new SuccessKilled();
                successKilled.setSeckillId(seckillId);
                successKilled.setUserId(userId);
                successKilled.setState((short) 1);
                successKilled.setCreateTime(new Timestamp(new Date().getTime()));
                //插入失败就回滚事务
                if (successKilledDao.save(successKilled) == 0) {
                    throw new RuntimeException("新增秒杀记录失败");
                }
                //返回秒杀记录
                return successKilled;
            } else {
                //库存不足
                throw new RuntimeException("库存不足");
            }
        }
    }

    //分布式架构:分布式zookeeper锁
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SuccessKilled executeByZkLock(long seckillId, long userId, String md5) {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            throw new RuntimeException("秒杀失败，涉嫌数据被修改");
        }
        //查询购买行为
        int insertCount = successKilledDao.getState(seckillId, userId);
        if (insertCount >= 0) {
            throw new RuntimeException("重复秒杀");
        } else {
            boolean zkLock = false;
            try {
                //获取zk锁,3秒
                zkLock = ZkLock.acquire(3, TimeUnit.SECONDS);
                if (zkLock) {
                    //查库存
                    Long number = seckillDao.getNumberById(seckillId);
                    //库存足够
                    if (number > 0) {
                        //更新商品库存就回滚事务
                        if (seckillDao.updateNumberDecrement(seckillId, new Date()) == 0) {
                            throw new RuntimeException("更新商品库存失败");
                        }
                        //新增秒杀记录
                        SuccessKilled successKilled = new SuccessKilled();
                        successKilled.setSeckillId(seckillId);
                        successKilled.setUserId(userId);
                        successKilled.setState((short) 1);
                        successKilled.setCreateTime(new Timestamp(new Date().getTime()));
                        //插入失败就回滚事务
                        if (successKilledDao.save(successKilled) == 0) {
                            throw new RuntimeException("新增秒杀记录失败");
                        }
                        //返回秒杀记录
                        return successKilled;
                    } else {
                        //库存不足
                        throw new RuntimeException("库存不足");
                    }
                } else {
                    throw new RuntimeException("获取zk锁失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (zkLock) {
                    //释放锁
                    ZkLock.release();
                }
            }
            return null;
        }
    }

    //分布式架构:分布式redis锁
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SuccessKilled executeByRedlock(long seckillId, long userId, String md5) {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            throw new RuntimeException("秒杀失败，涉嫌数据被修改");
        }
        //查询购买行为
        int insertCount = successKilledDao.getState(seckillId, userId);
        if (insertCount >= 0) {
            throw new RuntimeException("重复秒杀");
        } else {
            boolean redlock = false;
            try {
                //尝试获取锁，最多等待3秒，上锁以后10秒自动解锁（实际项目中推荐这种，以防出现死锁）
                redlock = RedLock.tryLock(seckillId + "", TimeUnit.SECONDS, 3, 10);
                if (redlock) {
                    //查库存
                    Long number = seckillDao.getNumberById(seckillId);
                    if (number >= 0) {
                        //更新商品库存就回滚事务
                        if (seckillDao.updateNumberDecrement(seckillId, new Date()) == 0) {
                            throw new RuntimeException("更新商品库存失败");
                        }
                        //新增秒杀记录
                        SuccessKilled successKilled = new SuccessKilled();
                        successKilled.setSeckillId(seckillId);
                        successKilled.setUserId(userId);
                        successKilled.setState((short) 1);
                        successKilled.setCreateTime(new Timestamp(new Date().getTime()));
                        //插入失败就回滚事务
                        if (successKilledDao.save(successKilled) == 0) {
                            throw new RuntimeException("新增秒杀记录失败");
                        }
                        //返回秒杀记录
                        return successKilled;
                    } else {
                        //库存不足
                        throw new RuntimeException("库存不足");
                    }
                } else {
                    throw new RuntimeException("获取zk锁失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (redlock) {
                    //释放锁
                    RedLock.unlock(seckillId + "");
                }
            }
            return null;
        }
    }

    //获取md5
    private String getMd5(long seckillId) {
        String slat = "ahkacfku347tq73trkca7tktxe7fxf^%(*&^(*^IU%FU%EYWXST#$JHGKJGJfjytrj";
        String base = seckillId + "/" + slat;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }
}
