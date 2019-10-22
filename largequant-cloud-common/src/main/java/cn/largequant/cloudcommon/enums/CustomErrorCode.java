package cn.largequant.cloudcommon.enums;

public enum CustomErrorCode {

    QUESTION_NOT_FOUND(2001, "你找到问题不在了，要不要换个试试？"),
    TARGET_PARAM_NOT_FOUND(2002, "未选中任何问题或评论进行回复"),
    NO_LOGIN(2003, "当前操作需要登录，请登陆后重试"),
    SYS_ERROR(2004, "服务冒烟了，要不然你稍后再试试！！！"),
    TYPE_PARAM_WRONG(2005, "评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006, "回复的评论不存在了，要不要换个试试？"),
    CONTENT_IS_EMPTY(2007, "输入内容不能为空"),
    READ_NOTIFICATION_FAIL(2008, "兄弟你这是读别人的信息呢？"),
    NOTIFICATION_NOT_FOUND(2009, "消息莫非是不翼而飞了？"),
    FILE_UPLOAD_FAIL(2010, "图片上传失败"),
    MAIL_PHONE_FORMAT_WRONG(2011, "邮箱或手机格式不正确"),
    PASSWORD_WRONG(2012, "密码不正确"),
    USER_NOT_EXIST(2013, "用户不存在！"),
    USERNAME_NOT_EXIST(2021, "用户名不存在！"),
    PASSWORD_FIVE_TIMES_WORNG(2014, "输入密码错误超过5次，请稍后重试"),
    PHONE_WRONG(2015, "手机不正确"),
    VERIFYCODE_WRONG(2016, "验证码不正确"),
    MAIL_WORNG(2017, "邮箱不正确"),
    MAIL_HAS_EXIST(2018, "邮箱已被注册"),
    PHONE_HAS_EXIST(2019, "手机号已被注册"),
    VERIFYCODE_TYPE_WRONG(2020, "短信验证码类型不匹配");

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    private Integer code;
    private String message;

    CustomErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }
}
