package com.fir.flow.config.result;

import lombok.Getter;


/**
 * @author fir
 */

@Getter
public enum AjaxResultCode implements StatusCode {
    /**
     * 请求成功
     */
    SUCCESS(200, "请求成功"),
    /**
     * 登录成功
     */
    SUCCESS_LOGIN(200, "登录成功"),
    /**
     * 登出成功
     */
    SUCCESS_LOGOUT(200, "登出成功"),
    /**
     * 启动成功
     */
    SUCCESS_FLOW_START(200, "启动成功"),
    /**
     * 暂无数据
     */
    NO_DATA(200, "暂无数据"),
    /**
     * 请求失败
     */
    LOSE_EFFICACY(400, "请求失败"),
    /**
     * 流程启动失败
     */
    LOSE_FLOW_START(400, "流程启动失败"),
    /**
     * 流程启动失败
     */
    LOSE_FLOW_STOP(400, "流程取消失败"),
    /**
     * 表单数据错误
     */
    LOSE_FROM_DATA(400, "表单数据错误"),
    /**
     * 数据已绑定无法重复绑定
     */
    ALREADY_BOUND(400, "已绑定"),
    /**
     * 无权访问!
     */
    ACCESS_DENIED(401, "无权访问!"),
    /**
     * 账号或密码为空
     */
    NULL_LOGIN_DATA(400, "账号或密码为空!"),
    /**
     * 数据库无数据
     */
    NULL_DATA(600, "数据库无数据"),
    /**
     * 缺少关键参数
     */
    MISS_KEY_PARA(601, "缺少关键参数"),
    /**
     * 请在模板里添加数据
     */
    TEMPLATE_NO_DATA(602, "请在模板里添加数据"),
    /**
     * 请求失败
     */
    FAILED(500, "请求失败"),
    /**
     * 请求失败
     */
    FAILED_EXC(1000, "请求失败"),
    /**
     * 参数校验失败
     */
    VALIDATE_ERROR(1002, "参数校验失败"),
    /**
     * 响应返回包装失败
     */
    RESPONSE_PACK_ERROR(1003, "响应返回包装失败");

    private final int code;
    private final String msg;

    AjaxResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}