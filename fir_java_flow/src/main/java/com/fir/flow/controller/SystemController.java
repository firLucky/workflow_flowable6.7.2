package com.fir.flow.controller;


import com.fir.flow.config.result.AjaxResult;
import com.fir.flow.config.result.AjaxResultCode;
import com.fir.flow.service.ISystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * @author fir
 * @date 2022/10/4 23:20
 */
@Api(tags = "系统登录")
@RestController
@RequestMapping("/auth")
public class SystemController {

    @Resource
    private ISystemService iSystemService;


    @ApiOperation("登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", defaultValue = "fir", dataTypeClass = String.class),
            @ApiImplicitParam(name = "password", value = "密码", defaultValue = "fir27", dataTypeClass = String.class),
    })
    @PostMapping("/login")
    public AjaxResult login(String username, String password){
        AjaxResult result;
        if(StringUtils.isNoneBlank(username) && StringUtils.isNoneBlank(password)){
            result = iSystemService.login(username, password);
        }else {
            result = AjaxResult.error(AjaxResultCode.NULL_LOGIN_DATA);
        }

        return result;
    }


    @ApiOperation("登出接口")
    @PostMapping("/logout")
    public AjaxResult logout(HttpServletRequest request){
        iSystemService.logout(request);

        return AjaxResult.success(AjaxResultCode.SUCCESS_LOGOUT);
    }
}
