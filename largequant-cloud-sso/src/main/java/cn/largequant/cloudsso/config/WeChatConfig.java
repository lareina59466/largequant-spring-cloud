package cn.largequant.cloudsso.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:config/wechat.properties", encoding = "UTF-8")
@ConfigurationProperties(prefix = "wechat")
@Getter
public class WeChatConfig {

    //  公众号appid
    private String appId;

    //  公众号秘钥
    private String appSecret;

    //  开放平台appid
    private String openAppid;

    //  开放平台appsecret
    private String openAppSecret;

    //  开放平台回调url
    private String openRedirectUrl;

    //  微信开放平台二维码连接
    private String openQrcodeUrl;

    //  开放平台获取access_token地址
    private String openAccessTokenUrl;

    //  获取用户信息
    private String openUserInfoUrl;

}
