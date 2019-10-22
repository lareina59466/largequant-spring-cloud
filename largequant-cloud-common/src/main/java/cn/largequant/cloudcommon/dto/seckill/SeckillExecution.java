package cn.largequant.cloudcommon.dto.seckill;

import cn.largequant.cloudcommon.entity.seckill.SuccessKilled;
import lombok.*;

/**
 * 秒杀执行结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillExecution {

    private long seckillId;

    private long userId;

    //秒杀状态
    private int state;

    //状态详情
    private String stateInfo;

}
