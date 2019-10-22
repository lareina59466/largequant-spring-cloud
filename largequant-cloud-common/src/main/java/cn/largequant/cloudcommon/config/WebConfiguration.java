package cn.largequant.cloudcommon.config;

import cn.largequant.cloudcommon.intercepter.LoginIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class WebConfiguration implements WebMvcConfigurer {

    @Autowired
    private LoginIntercepter loginIntercepter;

    // 注册拦截器(拦截器真正的加到controller上)
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // loginRequiredInterceptor在passportInterceptor后加入
        // 先使用passportInterceptor在cookie中寻找ticket
        // 再在loginRequiredInterceptor判断用户是否需要记录当前url并跳转到登录
        // addPathPatterns("user/*")，只有用户界面未登录时，才记录url(其实站内的url都应该记录，演示方便)
        registry.addInterceptor(loginIntercepter).addPathPatterns("user/*");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
