package cn.largequant.cloudcommon.constant.sso;

public interface LoginConst {

    String APPLICATION_NAME = "LOGIN-USER-SERVICE";

    /**
     * 登录类型
     */
    interface LoginType {
        //  正常登录(手机/邮箱)
        String PHONE_EMAIL = "1";
        //  短信登录
        String SHORT_MESSAGE = "2";
        //  第三方登录
        String THIRD_PART = "3";
    }

    /**
     * 短信验证类型
     */
    interface SmsType {
        //  登录
        String LOGIN_TYPE = "1";
        //  注册
        String REGISTER_TYPE = "2";
        //  忘记密码
        String FORGET_PASSWORD_TYPE = "3";
        //  绑定用户
        String BIND_USER_TYPE = "4";
        //  取消绑定用户
        String CANCEL_BIND_USER_TYPE = "5";
    }

    /**
     * 短信发送状态
     */
    interface SmsStatus {
        //  发送
        String SEND = "1";
        //  证明
        String PROVING = "2";
        //  无效
        String INVALIDATE = "3";
    }


    /**
     * 邮件验证类型
     */
    interface LoginUserMailType {
        //  找回密码
        String FORGET_PASSWORD_TYPE = "1";
        //  绑定邮箱
        String BIND_MAIL_TYPE = "2";
        //  取消绑定邮箱
        String CANCEL_BIND_MAIL_TYPE = "3";
    }

    /**
     * 用户状态类型
     */
    interface UserStatus {
        //  正常状态
        int NORMAL_STATUS = 1;
        //  冻结
        int FREEZE_STATUS = 2;
        //  密码次数输入过多
        int MORE_TIMES_STSTUS = 3;
        //  废弃
        int ABANDON_STATUS = 4;
    }

}
