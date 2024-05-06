package com.fir.flow.service;


import com.fir.flow.config.result.AjaxResult;
import javax.servlet.http.HttpServletRequest;


/**
 * @author fir
 * @date 2022/10/4 23:23
 */
public interface ISystemService {


    /**
     * 用户登录
     *
     * @param username 用户名称
     * @param password 用户密码
     * @return 登录成功以及信息/登录失败
     */
    AjaxResult login(String username, String password);


    /**
     * 用户登出
     *
     * @param request 请求头
     */
    void logout(HttpServletRequest request);
}
