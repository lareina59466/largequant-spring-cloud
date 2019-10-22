package cn.largequant.cloudseckill.dao;

import cn.largequant.cloudcommon.entity.seckill.Seckill;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SeckillDao {

    //分页查询所有秒杀商品
    List<Seckill> getSeckillList(@Param("offset") int offset, @Param("limit") int limit);

    //查询单个秒杀商品
    Seckill getSeckillById(@Param("seckillId") long seckillId);

    //查秒杀商品的库存
    long getNumberById(@Param("seckillId") long seckillId);

    //查秒杀商品的库存
    long getNumberByIdForUpdate(@Param("seckillId") long seckillId);

    //减库存
    int updateNumberDecrement(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    //减库存
    int updateNumberDecrementByVersion(@Param("seckillId") long seckillId, @Param("version") int version, @Param("killTime") Date killTime);

}
