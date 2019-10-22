package cn.largequant.cloudauth.dao.sysuser;

import cn.largequant.cloudcommon.entity.security.SysUserEntity;
import org.springframework.stereotype.Repository;

/**
 * 用户Dao接口类
 *
 * @author YWH
 */
@Repository
public interface SysUserDao {

    /**
     *  根据用户名查询用户信息
     * @param username 用户名
     * @return 用户信息
     */
    SysUserEntity selectByUserName(String username);

}
