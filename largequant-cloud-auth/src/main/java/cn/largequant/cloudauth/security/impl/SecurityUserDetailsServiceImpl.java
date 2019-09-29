package cn.largequant.cloudauth.security.impl;

import cn.largequant.cloudauth.dao.sysuser.SysUserDao;
import cn.largequant.cloudcommon.entity.SysRoleEntity;
import cn.largequant.cloudcommon.entity.SysUserEntity;
import cn.largequant.cloudcommon.entity.security.SecurityUserEntity;
import cn.largequant.cloudcommon.util.MyExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CreateTime: 2019-06-30 17:43
 * ClassName: SecurityUserDetailsServiceImpl
 * Package: cn.largequant.cloudauth.security.impl
 * Describe:
 * UserDetailService的实现类
 * 这个@Primary表示这个类所继承的接口有多个实现类，当不知道引入哪个的时候，优先使用@Primary所注解的类
 * @author YWH
 */
@Primary
@Service
public class SecurityUserDetailsServiceImpl implements UserDetailsService {

    /** 用户查询接口 */
    private SysUserDao sysUserDao;

    @Autowired
    public SecurityUserDetailsServiceImpl(SysUserDao sysUserDao) {
        this.sysUserDao = sysUserDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUserEntity sysUserEntity = sysUserDao.selectByUserName(username);
        if(sysUserEntity != null){
            // stream java8的新特性，Stream 使用一种类似用 SQL 语句从数据库查询数据的直观方式来提供一种对 Java 集合运算和表达的高阶抽象。
            // 参考http://www.runoob.com/java/java8-streams.html
            List<SimpleGrantedAuthority> collect = sysUserEntity.getRoles().stream().map(SysRoleEntity::getSysRoleName)
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            return new SecurityUserEntity(sysUserEntity.getSysUserPassword(),sysUserEntity.getSysUserName(),sysUserEntity.getSysUserState(),collect);
        }
        throw MyExceptionUtils.mxe(String.format("'%s'.这个用户不存在", username));
    }

}
