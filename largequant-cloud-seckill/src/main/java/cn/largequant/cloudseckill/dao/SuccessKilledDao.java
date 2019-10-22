package cn.largequant.cloudseckill.dao;

import cn.largequant.cloudcommon.entity.seckill.SuccessKilled;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SuccessKilledDao {

    //根据id查询successKilled 并携带产品对象
    SuccessKilled getByUserId(@Param("seckillId") long seckillId, @Param("userId") long userId);

    //查询秒杀商品卖出数量
    Long getNumberById(@Param("seckillId") long seckillId);

    //查找购买记录
    short getState(@Param("seckillId") long seckillId, @Param("userId") long userId);

    //新增秒杀成功记录
    int save(@Param("successKilled") SuccessKilled successKilled);

}
