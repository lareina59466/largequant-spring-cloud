package cn.largequant.cloudcommon.exception;

import cn.largequant.cloudcommon.enums.CustomErrorCode;

public class CustomException extends RuntimeException {

    private String message;
    private Integer code;

    public CustomException(CustomErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

}
