package cn.largequant.cloudcommon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecKillVO {

    //秒杀id
    private Long seckillId;

    //秒杀商品名称
    private String name;

    //商品剩余库存
    private Integer number;

    //秒杀开始时间
    private Date startTime;

    //秒杀结束时间
    private Date endTime;

    //创建时间
    private Date createTime;

    //秒杀开始时间时间戳
    private Long startTimestamp;

    //秒杀结束时间时间戳
    private Long endTimestamp;

}
