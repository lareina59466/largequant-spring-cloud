package cn.largequant.cloudauth.service.impl;

import cn.largequant.cloudauth.dao.sysuser.SysUserDao;
import cn.largequant.cloudauth.service.SysUserService;
import cn.largequant.cloudcommon.entity.security.SysUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

/**
 * 用户sevice实现类
 *
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    private SysUserDao sysUserDao;

    public SysUserServiceImpl(){}

    @Autowired
    public SysUserServiceImpl(SysUserDao sysUserDao,AuthenticationManager authenticate){
        this.sysUserDao = sysUserDao;
    }

    /**
     * 获取用户详细信息
     * @param username 用户名
     * @return 实体类
     */
    @Override
    public SysUserEntity findUserInfo(String username) {
        return sysUserDao.selectByUserName(username);
    }

    /**
     * 用户登陆
     * @param username 用户名
     * @param password 密码
     * @return 登陆成功 返回token
     */
    @Override
    public String login(String username, String password) throws AuthenticationException {
        return  "登录成功";
    }

}
