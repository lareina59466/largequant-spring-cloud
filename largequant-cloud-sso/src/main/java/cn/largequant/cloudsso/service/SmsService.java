package cn.largequant.cloudsso.service;

import java.util.Map;

public interface SmsService {

    //  创建手机验证码
    String createPhoneVerifyCode(String phonePrefix, String phone);

    //  获取手机验证码
    String getPhoneVerifyCode(String phonePrefix, String phone);

    //  检查手机验证码是否正确
    boolean checkPhoneVerifyCode(String phonePrefix, String phone, String verifyCode);

    //发送短信
    Map<String, Object> sendMessage(String phone, String shortMessageType);

}
