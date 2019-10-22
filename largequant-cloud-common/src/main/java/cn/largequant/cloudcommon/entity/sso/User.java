package cn.largequant.cloudcommon.entity.sso;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * 基础用户
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    //用户id
    private long userId;

    private String nickName;

    private String avatar;

    private String sign;

    private Integer sex;

    private String city;

    private Date createTime;

    private String lastLoginIp;

    private Date lastLoginTime;

    //    //用户登录的token
    //    private String token;
    //    //token过期时间
    //    private Date expireIn;
    //    //登录失败次数
    //    private int tryTimes;
}
