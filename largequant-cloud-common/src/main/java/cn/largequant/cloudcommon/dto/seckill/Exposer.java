package cn.largequant.cloudcommon.dto.seckill;

import lombok.Data;
import lombok.ToString;

/**
 * 秒杀暴露
 */
@Data
public class Exposer {

    private Boolean exposed;

    //一种加密措施
    private String md5;

    private long seckillId;

    //系统当前时间(毫秒）
    private long now;

    private long start;

    private long end;

}
