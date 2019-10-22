package cn.largequant.cloudcommon.constant.seckill;

/**
 * Redis前后缀
 */
public class RedisConstants {

    /**
     * redis key prefix
     */
    public static final String SECKILL_PREFIX = "seckill:";

    /**
     * redis key expireTime
     */
    public static final Long SECKILL_EXPIRE_TIME = 3600L;

    /**
     * LOCK_EXPIRE_TIME
     */
    public static final Long LOCK_EXPIRE_TIME = 10 * 1000L;

}
