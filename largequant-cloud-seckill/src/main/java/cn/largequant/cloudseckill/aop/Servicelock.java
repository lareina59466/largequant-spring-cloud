package cn.largequant.cloudseckill.aop;

import java.lang.annotation.*;

/**
 * 自定义同步锁注解
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Servicelock {
    String description() default "";
}
