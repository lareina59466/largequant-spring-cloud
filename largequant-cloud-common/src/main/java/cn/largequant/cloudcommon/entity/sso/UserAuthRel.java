package cn.largequant.cloudcommon.entity.sso;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 用户验证关联
 */
@Data

@NoArgsConstructor
@AllArgsConstructor
public class UserAuthRel {

    private long id;

    //用户表id
    private long userId;

    //验证表id
    private long authId;

    //验证类型(local、third)
    private int authType;

}
