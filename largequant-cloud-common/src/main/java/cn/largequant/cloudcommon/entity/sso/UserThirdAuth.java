package cn.largequant.cloudcommon.entity.sso;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 第三方用户
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserThirdAuth {

    //用户id
    private long authId;

    //第三方用户唯一标识
    private String openId;

    //第三方平台标识(qq、wechat)
    private int loginType;

    //第三方获取的access_token,校验使用
    private String accessToken;

}
