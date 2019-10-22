package cn.largequant.cloudsso.service;

public interface MailService {

    void sendTemplateEmail(String email, String verifyCode);

}

