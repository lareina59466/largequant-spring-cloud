package cn.largequant.cloudcommon.entity.seckill;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * 秒杀商品
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seckill {

    private long id;

    private String name;

    private int number;

    private Date startTime;

    private Date endTime;

    private Date createTime;

    private int version;

}
