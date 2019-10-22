package cn.largequant.cloudcommon.entity.sso;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 本地用户
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLocalAuth {

    //验证表id
    private long authId;

    private String username;

    private String password;

    private String phone;

    private String salt;

}
