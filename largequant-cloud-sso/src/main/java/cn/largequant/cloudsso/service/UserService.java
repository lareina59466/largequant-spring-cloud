package cn.largequant.cloudsso.service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface UserService {

    //  注册
    Map<String, Object> register(String username, String password, HttpServletRequest request);

    //  正常登录
    Map<String, Object> normalLogin(String username, String password, HttpServletRequest request);

    //  短信验证登陆
    Map<String, Object> smsLogin(String phone, String verifyCode, HttpServletRequest request);

    //  微信登陆
    Map<String, Object> wxLogin(String code, HttpServletRequest request);

    //  绑定微信
    Map<String, Object> bindWx(String code, HttpServletRequest request);

    //  绑定手机号
    Map<String, Object> bindPhone(String username, String password, String phone, HttpServletRequest request);

    //  修改密码
    Map<String, Object> modifyPassword(String phone, String newPassword, String verifyCode);


}
