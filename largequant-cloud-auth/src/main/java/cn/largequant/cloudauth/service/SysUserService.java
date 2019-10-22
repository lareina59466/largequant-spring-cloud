package cn.largequant.cloudauth.service;

import cn.largequant.cloudcommon.entity.security.SysUserEntity;
import org.springframework.security.core.AuthenticationException;

/**
 * 用户逻辑层
 *
 */
public interface SysUserService {

    /**
     * 获取用户详细信息
     * @param username 用户名
     * @return 实体类
     */
    SysUserEntity findUserInfo(String username);

    /**
     *
     * @param username 用户名
     * @param password 密码
     * @return 登陆成功
     */
    String login(String username, String password) throws AuthenticationException;

}
