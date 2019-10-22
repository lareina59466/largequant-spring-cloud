package cn.largequant.cloudcommon.entity.seckill;

import lombok.*;

import java.util.Date;

/**
 * 成功秒杀记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuccessKilled {

    private long id;

    private long seckillId;

    private long userId;

    private short state;

    private Date createTime;

}
