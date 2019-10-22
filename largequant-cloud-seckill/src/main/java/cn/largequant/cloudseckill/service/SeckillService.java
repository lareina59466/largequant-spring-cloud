package cn.largequant.cloudseckill.service;

import cn.largequant.cloudcommon.dto.seckill.SeckillExecution;
import cn.largequant.cloudcommon.entity.JsonResult;
import cn.largequant.cloudcommon.dto.seckill.Exposer;
import cn.largequant.cloudcommon.entity.seckill.Seckill;
import cn.largequant.cloudcommon.entity.seckill.SuccessKilled;

import java.util.List;

public interface SeckillService {

    //查询所有秒杀记录
    List<Seckill> getSeckillList();

    //查询单个秒杀记录
    Seckill getSeckillById(long seckillId);

    //查询秒杀售卖商品
    Long getSeckillCount(long seckillId);

    //先暴露秒杀接口
    Exposer exportSeckillUrl(long seckillId);

    //单体架构:Lock + 进程内队列
    SuccessKilled executeByAopLock(long seckillId, long userId, String md5);

    //单体架构:数据库乐观锁
    SuccessKilled executeByOptimisticLock(long seckillId, long userId, String md5);

    //单体架构:数据库悲观锁
    SuccessKilled executeByPessimisticLock(long seckillId, long userId, String md5);

    //分布式架构:分布式zookeeper锁
    SuccessKilled executeByZkLock(long seckillId, long userId, String md5);

    //分布式架构:分布式redis锁
    SuccessKilled executeByRedlock(long seckillId, long userId, String md5);

}


