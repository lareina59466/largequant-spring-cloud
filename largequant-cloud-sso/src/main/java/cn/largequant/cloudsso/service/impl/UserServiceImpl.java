package cn.largequant.cloudsso.service.impl;

import cn.largequant.cloudcommon.entity.sso.User;
import cn.largequant.cloudcommon.entity.sso.UserAuthRel;
import cn.largequant.cloudcommon.entity.sso.UserLocalAuth;
import cn.largequant.cloudcommon.entity.sso.UserThirdAuth;
import cn.largequant.cloudcommon.util.*;
import cn.largequant.cloudsso.config.WeChatConfig;
import cn.largequant.cloudcommon.constant.sso.RedisConst;
import cn.largequant.cloudsso.dao.UserAuthRelDao;
import cn.largequant.cloudsso.dao.UserLocalAuthDao;
import cn.largequant.cloudsso.dao.UserThirdAuthDao;
import cn.largequant.cloudsso.dao.UserDao;
import cn.largequant.cloudsso.service.SmsService;
import cn.largequant.cloudsso.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;


@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private UserDao userDao;
    private UserAuthRelDao userAuthRelDao;
    private UserLocalAuthDao userLocalAuthDao;
    private UserThirdAuthDao userThirdAuthDao;
    private SmsService smsService;
    private RedisUtil redisUtil;
    private WeChatConfig weChatConfig;

    @Autowired
    public UserServiceImpl(UserDao userDao, UserAuthRelDao userAuthRelDao, UserLocalAuthDao userLocalAuthDao, UserThirdAuthDao userThirdAuthDao, SmsService smsService, RedisUtil redisUtil, WeChatConfig weChatConfig) {
        this.userDao = userDao;
        this.userAuthRelDao = userAuthRelDao;
        this.userLocalAuthDao = userLocalAuthDao;
        this.userThirdAuthDao = userThirdAuthDao;
        this.smsService = smsService;
        this.redisUtil = redisUtil;
        this.weChatConfig = weChatConfig;
    }

    //  注册
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Map<String, Object> register(String username, String password, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        //  查询本地用户
        UserLocalAuth userLocalAuth = userLocalAuthDao.getByUsernameOrPhone(username);
        if (userLocalAuth != null) {
            map.put("msg", "用户名已经被注册");
            return map;
        }
        userLocalAuth = new UserLocalAuth();
        userLocalAuth.setUsername(username);
        userLocalAuth.setSalt(UUID.randomUUID().toString().substring(0, 5));
        userLocalAuth.setPassword(MD5Util.MD5(password + userLocalAuth.getSalt()));
        long authId = userLocalAuthDao.save(userLocalAuth);

        User user = new User();
        user.setNickName("无名小卒");
        user.setAvatar(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        user.setLastLoginTime(new Date());
        user.setLastLoginIp(IPUtil.getIpAddr(request));
        long userId = userDao.save(user);

        UserAuthRel userAuthRel = new UserAuthRel();
        userAuthRel.setUserId(userId);
        userAuthRel.setAuthId(authId);
        userAuthRel.setAuthType(1);
        userAuthRelDao.save(userAuthRel);

        User loginUser = userDao.getByUserId(userId);
        String token = JWTUtil.geneJsonWebToken(loginUser);
        map.put("msg", "注册成功");
        map.put("token", token);
        return map;
    }

    //  正常登录
    @Override
    public Map<String, Object> normalLogin(String username, String password, HttpServletRequest request) {
        // 使用map记录登录的错误信息(用于前端显示)
        Map<String, Object> map = new HashMap<>();
        //  查询本地用户
        UserLocalAuth userLocalAuth = userLocalAuthDao.getByUsernameOrPhone(username);
        if (userLocalAuth == null) {
            map.put("msg", "用户不存在");
            return map;
        }
        //  检查是否被冻结
        if (((String) redisUtil.getString(RedisConst.FROZEN_PREFIX + username)).equals("-1")) {
            map.put("msg", "用户被冻结，请5分钟之后再登录");
            return map;
        }
        //  错误密码次数a
        String wrongLogin = (String) redisUtil.getString(RedisConst.LOGIN_TIMES_PREFIX + username);
        String loginTimes = "0";
        if (StringUtils.isEmpty(wrongLogin)) {
            if (!MD5Util.MD5(password + userLocalAuth.getSalt()).equals(userLocalAuth.getPassword())) {
                //  密码错误-插入记录-提示错误
                redisUtil.setString(RedisConst.LOGIN_TIMES_PREFIX + username, loginTimes, RedisConst.PASSWORD_EFFECTIVE_TIME);
                map.put("msg", "密码错误");
                return map;
            }
        } else {
            Integer wrongLoginTime = (Integer) redisUtil.getString(RedisConst.LOGIN_TIMES_PREFIX + username);
            if (wrongLoginTime < 5) {
                //  错误次数自增
                if (!MD5Util.MD5(password + userLocalAuth.getSalt()).equals(userLocalAuth.getPassword())) {
                    redisUtil.setString(RedisConst.LOGIN_TIMES_PREFIX + username, String.valueOf(wrongLoginTime + 1), RedisConst.PASSWORD_EFFECTIVE_TIME);
                    map.put("msg", "密码错误");
                    return map;
                }
            } else {
                //  达到5次-冻结用户-redis冻结-5分钟
                redisUtil.setString(RedisConst.FROZEN_PREFIX + username, "-1", 5 * 60);
            }
        }
        User user = userDao.getByAuthId(userLocalAuth.getAuthId(), 1);
        //  登录成功
        user.setLastLoginIp(IPUtil.getIpAddr(request));
        user.setLastLoginTime(new Date());
        userDao.update(user);
        //生成jwt
        String token = JWTUtil.geneJsonWebToken(user);
        map.put("token", token);
        return map;
    }

    //  短信验证登陆
    @Override
    public Map<String, Object> smsLogin(String phone, String verifyCode, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        boolean isPass = smsService.checkPhoneVerifyCode(RedisConst.SMS_LOGIN_PREFIX, phone, verifyCode);
        if (isPass) {
            UserLocalAuth userLocalAuth = userLocalAuthDao.getByPhone(phone);
            if (userLocalAuth == null) {
                map.put("msg", "该手机号还未注册，请先注册");
                return map;
            }
            User user = userDao.getByAuthId(userLocalAuth.getAuthId(), 1);
            //  登录成功
            user.setLastLoginIp(IPUtil.getIpAddr(request));
            user.setLastLoginTime(new Date());
            userDao.update(user);
            //生成jwt
            String token = JWTUtil.geneJsonWebToken(user);
            map.put("token", token);
            return map;
        } else {
            map.put("msg", "验证不通过");
            return map;
        }
    }

    //  微信登陆
    @Override
    public Map<String, Object> wxLogin(String code, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        //  获取accessToken地址
        String accessTokenUrl = String.format(weChatConfig.getOpenAccessTokenUrl(), weChatConfig.getOpenAppid(), weChatConfig.getOpenAppSecret(), code);
        //  请求获取响应参数
        Map<String, Object> baseMap = HttpUtil.doGet(accessTokenUrl);
        if (baseMap == null || baseMap.isEmpty()) {
            map.put("msg", "微信登录失败");
            return map;
        }
        //  提取
        String accessToken = (String) baseMap.get("access_token");
        String openId = (String) baseMap.get("openid");
        //  微信用户是否登录过
        UserThirdAuth userThirdAuth = userThirdAuthDao.getByOpenIdAndAccesstoken(openId, accessToken);
        if (userThirdAuth == null) {
            map.put("msg", "您是第一次登录，请先绑定");
            return map;
        }
        User user = userDao.getByAuthId(userThirdAuth.getAuthId(), 2);
        //  登录成功
        user.setLastLoginIp(IPUtil.getIpAddr(request));
        user.setLastLoginTime(new Date());
        userDao.update(user);
        //生成jwt
        String token = JWTUtil.geneJsonWebToken(user);
        map.put("msg", "登录成功");
        map.put("token", token);
        return map;
    }

    //  绑定微信
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Map<String, Object> bindWx(String code, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        //  获取accessToken地址
        String accessTokenUrl = String.format(weChatConfig.getOpenAccessTokenUrl(), weChatConfig.getOpenAppid(), weChatConfig.getOpenAppSecret(), code);
        //  请求获取响应参数
        Map<String, Object> baseMap = HttpUtil.doGet(accessTokenUrl);
        if (baseMap == null || baseMap.isEmpty()) {
            map.put("msg", "微信登录失败");
            return map;
        }
        //  提取
        String accessToken = (String) baseMap.get("access_token");
        String openId = (String) baseMap.get("openid");
        //  获取userInfo地址
        String userInfoUrl = String.format(weChatConfig.getOpenUserInfoUrl(), accessToken, openId);
        //  请求获取响应参数
        Map<String, Object> baseUserMap = HttpUtil.doGet(userInfoUrl);
        //  提取微信用户信息
        String nickname = (String) baseUserMap.get("nickname");
        Double sexTemp = (Double) baseUserMap.get("sex");
        int sex = sexTemp.intValue();
        String headimgurl = (String) baseUserMap.get("headimgurl");
        //  解决乱码
        try {
            nickname = new String(nickname.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //  包装
        UserThirdAuth userThirdAuth = new UserThirdAuth();
        userThirdAuth.setOpenId(openId);
        userThirdAuth.setAccessToken(accessToken);
        userThirdAuth.setLoginType(1);
        long authId = userThirdAuthDao.save(userThirdAuth);

        User user = new User();
        user.setNickName(nickname);
        user.setAvatar(headimgurl);
        user.setSex(sex);
        user.setCreateTime(new Date());
        user.setLastLoginTime(new Date());
        user.setLastLoginIp(IPUtil.getIpAddr(request));
        long userId = userDao.save(user);

        UserAuthRel userAuthRel = new UserAuthRel();
        userAuthRel.setUserId(userId);
        userAuthRel.setAuthId(authId);
        userAuthRel.setAuthType(2);
        userAuthRelDao.save(userAuthRel);

        User loginUser = userDao.getByUserId(userId);
        String token = JWTUtil.geneJsonWebToken(loginUser);
        map.put("msg", "注册成功");
        map.put("token", token);
        return map;
    }

    //  绑定手机号
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Map<String, Object> bindPhone(String username, String password, String phone, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        return map;
    }

    //  修改密码
    @Override
    public Map<String, Object> modifyPassword(String phone, String newPassword, String verifyCode) {
        Map<String, Object> map = new HashMap<>();
        boolean isPass = smsService.checkPhoneVerifyCode(RedisConst.SMS_FORGET_PASSWORD_PREFIX, phone, verifyCode);
        if (isPass) {
            //  查询本地用户
            UserLocalAuth userLocalAuth = userLocalAuthDao.getByPhone(phone);
            if (userLocalAuth == null) {
                map.put("msg", "用户不存在");
                return map;
            }
            //  修改更新
            UserLocalAuth newUserLocalAuth = new UserLocalAuth();
            newUserLocalAuth.setPassword(newPassword);
            userLocalAuthDao.update(newUserLocalAuth);
            map.put("msg", "修改成功");
            return map;
        } else {
            map.put("msg", "验证不通过");
            return map;
        }
    }

}
