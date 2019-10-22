package cn.largequant.cloudcommon.entity;

import lombok.Data;

@Data
public class JsonResult<T> {

    private Boolean success;

    private Integer code;

    private String msg;

    private T data;

    public static <T> JsonResult success() {
        JsonResult<T> result = new JsonResult<>();
        result.setSuccess(true);
        result.setCode(0);
        result.setMsg("成功");
        return result;
    }

    public static <T> JsonResult success(T data) {
        JsonResult<T> result = new JsonResult<>();
        result.setSuccess(true);
        result.setCode(0);
        result.setMsg("成功");
        result.setData(data);
        return result;
    }

    public static <T> JsonResult error() {
        JsonResult<T> result = new JsonResult<>();
        result.setSuccess(false);
        result.setCode(-1);
        result.setMsg("失败");
        return result;
    }

    public static <T> JsonResult error(String message) {
        JsonResult<T> result = new JsonResult<>();
        result.setSuccess(false);
        result.setCode(-1);
        result.setMsg(message);
        return result;
    }

}
