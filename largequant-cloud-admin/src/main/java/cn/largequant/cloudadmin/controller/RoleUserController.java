package cn.largequant.cloudadmin.controller;

import cn.largequant.cloudadmin.base.Results;
import cn.largequant.cloudadmin.service.RoleUserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roleuser")
@Slf4j
public class RoleUserController {
    @Autowired
    private RoleUserService roleUserService;

    @PostMapping("/getRoleUserByUserId")
    @ApiOperation(value = "获取当前用户角色", notes = "获取当前用户角色")//描述
    @ApiImplicitParam(name = "userId",value = "用户Id", required = true)
    public Results getRoleUserByUserId(Integer userId) {
        log.info("getRoleUserByUserId（"+userId+"）");
        return roleUserService.getSysRoleUserByUserId(userId);
    }
}
