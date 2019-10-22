package cn.largequant.cloudsso.service.impl;

import cn.largequant.cloudcommon.enums.CustomErrorCode;
import cn.largequant.cloudcommon.exception.CustomException;
import cn.largequant.cloudcommon.util.RedisUtil;
import cn.largequant.cloudsso.dao.UserLocalAuthDao;
import cn.largequant.cloudcommon.entity.sso.UserLocalAuth;
import cn.largequant.cloudcommon.util.RegexUtil;
import cn.largequant.cloudcommon.constant.sso.RedisConst;
import cn.largequant.cloudsso.service.SmsService;
import com.alibaba.fastjson.JSONException;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class SmsServiceImpl implements SmsService {

    private final UserLocalAuthDao userLocalAuthDao;

    private final RedisUtil redisUtil;

    @Autowired
    public SmsServiceImpl(UserLocalAuthDao userLocalAuthDao, RedisUtil redisUtil) {
        this.userLocalAuthDao = userLocalAuthDao;
        this.redisUtil = redisUtil;
    }

    //  创建手机验证码
    @Override
    public String createPhoneVerifyCode(String smsPrefix, String phone) {
        //  获取用户
        UserLocalAuth userLocalAuth = userLocalAuthDao.getByPhone(phone);
        if (userLocalAuth == null) {
            throw new CustomException(CustomErrorCode.USER_NOT_EXIST);
        }
        //  redis插入验证码
        String key = smsPrefix + phone;
        String verifyCode = this.createRandom(true, 6);
        redisUtil.setString(key, verifyCode, RedisConst.SMS_VERIFY_CODE_EFFECTIVE_TIME);
        return verifyCode;
    }

    //  获取手机验证码
    @Override
    public String getPhoneVerifyCode(String smsPrefix, String phone) {
        String key = smsPrefix + phone;
        return (String) redisUtil.getString(key);
    }

    //  检查手机验证码是否正确
    @Override
    public boolean checkPhoneVerifyCode(String smsPrefix, String phone, String verifyCode) {
        String cacheVerifyCode = this.getPhoneVerifyCode(smsPrefix, phone);
        return !StringUtils.isEmpty(cacheVerifyCode) && cacheVerifyCode.equalsIgnoreCase(verifyCode);
    }

    //  发送短信
    @Override
    public Map<String, Object> sendMessage(String phone, String smsType) {
        if (StringUtils.isEmpty(phone) && !RegexUtil.checkMobile(phone)) {
            throw new CustomException(CustomErrorCode.PHONE_WRONG);
        }
        //  验证类型
        String redisSmsPrefix = null;
        if (smsType.equalsIgnoreCase(RedisConst.SMS_LOGIN_PREFIX)) {
            redisSmsPrefix = RedisConst.SMS_LOGIN_PREFIX;
        } else if (smsType.equalsIgnoreCase(RedisConst.SMS_REGISTER_PREFIX)) {
            redisSmsPrefix = RedisConst.SMS_REGISTER_PREFIX;
        } else if (smsType.equalsIgnoreCase(RedisConst.SMS_FORGET_PASSWORD_PREFIX)) {
            redisSmsPrefix = RedisConst.SMS_FORGET_PASSWORD_PREFIX;
        } else if (smsType.equalsIgnoreCase(RedisConst.SMS_BIND_PREFIX)) {
            redisSmsPrefix = RedisConst.SMS_BIND_PREFIX;
        } else if (smsType.equalsIgnoreCase(RedisConst.SMS_CANCEL_BIND_PREFIX)) {
            redisSmsPrefix = RedisConst.SMS_CANCEL_BIND_PREFIX;
        } else {
            throw new CustomException(CustomErrorCode.VERIFYCODE_TYPE_WRONG);
        }
        //  获取验证码
        String[] verifyCode = {this.createPhoneVerifyCode(redisSmsPrefix, phone)};
        redisUtil.setString(redisSmsPrefix + phone, verifyCode[0], 15 * 60);
        Map<String, Object> map = new HashMap<>();
        //  调用短信接口
        try {
            tencentSend(phone, verifyCode);
            map.put("msg","发送成功");
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg","发送失败");
            return map;
        }
    }

    //  腾讯短信
    private void tencentSend(String phone, String[] verifyCode) {
        //  腾讯云中本项目的AppID
        int appid = 1400237609;
        //  腾讯云中本项目的Appkey
        String appkey = "5f820024df2251c1d6d801903c733536";
        //  短信的模板ID
        int templateId = 7839;
        //  签名
        String smsSign = "LargeQuant平台";
        try {
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            SmsSingleSenderResult result = ssender.sendWithParam("86", phone, templateId, verifyCode, smsSign, "", "");
            System.out.println(result);
        } catch (HTTPException e) {
            // HTTP 响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // JSON 解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络 IO 错误
            e.printStackTrace();
        } catch (Exception e) {
            // 网络 IO 错误
            e.printStackTrace();
        }
    }

    //  创建指定数量的随机字符串
    private String createRandom(boolean numberFlag, int length) {
        String retStr = null;
        String NUMBER = "1234567890";
        String NUMBER_STRING = "1234567890abcdefghijkmnpqrstuvwxyz";
        String strTable = numberFlag ? NUMBER : NUMBER_STRING;
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 2) {
                bDone = false;
            }
        } while (bDone);
        return retStr;
    }

}
