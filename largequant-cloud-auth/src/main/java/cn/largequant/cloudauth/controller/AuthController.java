package cn.largequant.cloudauth.controller;

import cn.largequant.cloudauth.service.SysUserService;
import cn.largequant.cloudcommon.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统登录接口Controller
 *
 */
@RestController
@RequestMapping("auth")
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    private SysUserService sysUserService;

    public AuthController(){}

    @Autowired
    public AuthController(SysUserService sysUserService){
        this.sysUserService = sysUserService;
    }

    /**
     * 根据用户名查询用户信息
     * @param username  用户名
     * @return 用户信息
     */
    @GetMapping("selectUserByName")
    public Result selectUserByName(@RequestParam String username){
        return Result.successJson(sysUserService.findUserInfo(username));
    }

}
