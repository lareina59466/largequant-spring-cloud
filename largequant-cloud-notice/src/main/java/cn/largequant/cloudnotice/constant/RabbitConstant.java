package cn.largequant.cloudnotice.constant;

public class RabbitConstant {

    //交换机名称
    public final static String CONFIRM_EXCHANGE = "exchange.confirm";
    //队列
    public final static String COMMENT_QUEUE = "queue.comment";
    public final static String LIKE_QUEUE = "queue.like";
    public final static String SECKILL_QUEUE = "queue.seckill";
    //路由key
    public final static String RK_COMMENT = "comment";
    public final static String RK_LIKE = "like";
    public final static String RK_SECKILL = "seckill";

}
