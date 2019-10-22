package cn.largequant.cloudcommon.enums;

/**
 * 基础枚举类
 *
 */
public enum BaseEnum {


    /**
     * 基础枚举code值
     */
    SUCCESS("后台处理成功！",200),
    /**
     * 后台处理失败
     */
    FAIL("后台处理失败！",100),
    ;


    private String msg;

    private int code;

    BaseEnum(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


}
