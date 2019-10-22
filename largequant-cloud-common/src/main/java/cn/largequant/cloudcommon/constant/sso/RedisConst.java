package cn.largequant.cloudcommon.constant.sso;

public interface RedisConst {
    //  登录类型
    String LOGIN_TYPE_PREFIX = " LOGIN_TYPE_";
    //  注册类型
    String REGISTER_TYPE_PREFIX = "REGISTER_TYPE_";
    //  登录次数
    String LOGIN_TIMES_PREFIX = "LOGIN_TIMES_";
    //  用户Session
    String USER_SESSION_PREFIX = "USER_SESSION_";
    //  登录凭证
    String LOGIN_TOKEN = "LOGIN_TOKEN_";

    String FROZEN_PREFIX = "LOGIN_FROZEN_";

    //  短信验证码-登录
    String SMS_LOGIN_PREFIX = "SMS_LOGIN_";
    //  短信验证码-注册
    String SMS_REGISTER_PREFIX = "SMS_REGISTER_";
    //  短信验证码-忘记密码
    String SMS_FORGET_PASSWORD_PREFIX = "SMS_FORGET_PASSWORD_";
    //  短信验证码-绑定用户
    String SMS_BIND_PREFIX = "SMS_BIND_";
    //  短信验证码-取消绑定用户
    String SMS_CANCEL_BIND_PREFIX = "SMS_CANCEL_BIND_";

    //  邮箱验证码-注册
    String MAIL_REGISTER_PREFIX = "MAIL_REGISTER_";
    //  邮箱验证码-忘记密码
    String MAIL_FORGET_PASSWORD_PREFIX = "MAIL_FORGET_PASSWORD_";
    //  邮箱验证码-绑定用户
    String MAIL_BIND_PREFIX = "MAIL_BIND_";
    //  邮箱验证码-取消绑定用户
    String MAIL_CANCEL_BIND_PREFIX = "MAIL_CANCEL_BIND_";

    //  错误密码-过期时间
    long PASSWORD_EFFECTIVE_TIME = 15 * 60;
    //  登录凭证-过期时间
    long TOKEN_EFFECTIVE_TIME = 100000 * 60 * 60;
    //  邮箱验证码-过期时间
    long MAIL_VERIFY_CODE_EFFECTIVE_TIME = 100000 * 60 * 60;
    //  短信验证码-过期时间
    long SMS_VERIFY_CODE_EFFECTIVE_TIME = 100000 * 60 * 60;
}
