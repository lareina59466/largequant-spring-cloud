package cn.largequant.cloudadmin.service.impl;

import cn.largequant.cloudadmin.base.Results;
import cn.largequant.cloudadmin.dao.RoleUserDao;
import cn.largequant.cloudadmin.model.SysRoleUser;
import cn.largequant.cloudadmin.service.RoleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleUserServiceImpl implements RoleUserService {
    @Autowired
    private RoleUserDao roleUserDao;

    @Override
    public Results getSysRoleUserByUserId(Integer userId) {
        SysRoleUser sysRoleUser = roleUserDao.getSysRoleUserByUserId(userId);
        if(sysRoleUser != null){
            return Results.success(sysRoleUser);
        }else{
            return Results.success();
        }
    }
}
