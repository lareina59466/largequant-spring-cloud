package cn.largequant.cloudsso.controller;

import cn.largequant.cloudsso.config.WeChatConfig;
import cn.largequant.cloudcommon.constant.sso.RedisConst;
import cn.largequant.cloudsso.service.SmsService;
import cn.largequant.cloudsso.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;


@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private UserService userService;
    private SmsService smsService;
    private WeChatConfig weChatConfig;

    @Autowired
    public LoginController(UserService userService, SmsService smsService, WeChatConfig weChatConfig) {
        this.userService = userService;
        this.smsService = smsService;
        this.weChatConfig = weChatConfig;
    }

    // 登录注册页面
    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String regloginPage(@RequestParam(value = "next", required = false) String next, Model model) {
        model.addAttribute("next", next);
        return "login";
    }

    // 注册
    @RequestMapping(path = {"/reg"}, method = {RequestMethod.POST})
    public String reg(Model model,
                      @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam("next") String next,
                      @RequestParam(value = "remember", defaultValue = "false") boolean remember,
                      HttpServletResponse response, HttpServletRequest request) {
        try {
            Map<String, Object> map = userService.register(username, password, request);
            // 注册完后，自动登录(一定有t票(和登录不同))
            // 将t票，放入浏览器cookie
            Cookie cookie = new Cookie("ticket", map.get("token").toString());
            cookie.setPath("/");
            if (remember) {
                // 根据是否点击remember按钮，设置cookie保存时间(5天)
                cookie.setMaxAge(3600 * 24 * 5);
            }
            response.addCookie(cookie);
            if (StringUtils.isNotBlank(next)) {
                return "redirect:" + next;
            }
            return "redirect:/";
        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            model.addAttribute("msg", "服务异常");
            return "login";
        }
    }

    // 正常登录
    @RequestMapping(path = {"/login"}, method = RequestMethod.POST)
    public String login(@RequestParam(value = "username") String username,
                        @RequestParam(value = "password") String password,
                        @RequestParam(value = "next", required = false) String next,
                        @RequestParam(value = "remember", defaultValue = "false") boolean remember,
                        HttpServletResponse response, HttpServletRequest request, Model model) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return "login";
        }
        // 验证登录
        Map<String, Object> map = userService.normalLogin(username, password, request);
        if (map.containsKey("token")) {
            // 将t票，放入浏览器cookie
            Cookie cookie = new Cookie("token", map.get("token").toString());
            cookie.setPath("/");
            // 根据是否点击remember按钮，设置cookie保存时间(5天)
            if (remember) {
                cookie.setMaxAge(3600 * 24 * 5);
            }
            response.addCookie(cookie);
            //跳转
            if (StringUtils.isNotBlank(next)) {
                return "redirect:" + next;
            }
            return "redirect:/";
        } else {
            // 无t票未成功验证t票，记录错误信息
            // 返回登录页面
            model.addAttribute("msg", map.get("msg"));
            return "login";
        }
    }

    //  短信验证登陆
    @RequestMapping(path = {"/smsLogin"}, method = RequestMethod.POST)
    public String smsLogin(@RequestParam("phone") String phone,
                           @RequestParam("verifyCode") String verifyCode,
                           @RequestParam(value = "next", required = false) String next,
                           HttpServletResponse response, HttpServletRequest request, Model model) {
        Map<String, Object> map = userService.smsLogin(phone, verifyCode, request);
        if (map.containsKey("token")) {
            Cookie cookie = new Cookie("token", map.get("token").toString());
            cookie.setPath("/");
            cookie.setMaxAge(3600 * 24);
            response.addCookie(cookie);
            //跳转
            if (StringUtils.isNotBlank(next)) {
                return "redirect:" + next;
            }
            return "redirect:/";
        } else {
            model.addAttribute("msg", map.get("msg"));
            return "login";
        }
    }

    //  获取短信验证码
    @RequestMapping(path = {"/sendSms"}, method = {RequestMethod.GET})
    public void sendSms(@RequestParam("phone") String phone) {
        smsService.sendMessage(RedisConst.SMS_LOGIN_PREFIX, phone);
    }

    //  进入微信扫码页
    @RequestMapping(path = {"/wxLogin"}, method = {RequestMethod.GET})
    public String wxLogin(@RequestParam(value = "next", required = false) String next) {
        try {
            //  回调地址
            String callbackUrl = URLEncoder.encode(weChatConfig.getOpenRedirectUrl(), "GBK");
            //  二维码地址
            String qrcodeUrl = String.format(weChatConfig.getOpenQrcodeUrl(), weChatConfig.getOpenAppid(), callbackUrl, next);
            //  跳转到扫码页面
            return "redirect:" + qrcodeUrl;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "redirect:" + next;
        }
    }

    //  微信扫码成功回调
    @RequestMapping(path = {"/wxlogin/callback"}, method = {RequestMethod.GET})
    public void wxLoginCallback(@RequestParam("code") String code,
                                @RequestParam("state") String next,
                                HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.wxLogin(code, request);
            if (map.containsKey("token")) {
                Cookie cookie = new Cookie("token", map.get("token").toString());
                cookie.setPath("/");
                cookie.setMaxAge(3600 * 24);
                response.addCookie(cookie);
                response.sendRedirect(next);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 修改密码
    @RequestMapping(path = {"/modifyPwd"}, method = RequestMethod.POST)
    public String modifyPassword(@RequestParam(value = "phone") String phone,
                                 @RequestParam(value = "newPassword") String newPassword,
                                 @RequestParam(value = "verifyCode") String verifyCode,
                                 Model model) {
        Map<String, Object> map = userService.modifyPassword(phone, newPassword, verifyCode);
        model.addAttribute("msg", map.get("msg").toString());
        return "login";
    }

    //  登出
    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET})
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }

}
