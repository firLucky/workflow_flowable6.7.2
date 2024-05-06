package com.fir.flow.config.result;

import java.util.HashMap;


/**
 * 操作消息JSON提醒
 * 
 * @author fir
 */
public class AjaxResult extends HashMap<String, Object>
{
    private static final long serialVersionUID = 1L;

    /** 状态码 */
    public static final String CODE_TAG = "code";

    /** 返回内容 */
    public static final String MSG_TAG = "msg";

    /** 数据对象 */
    public static final String DATA_TAG = "data";

    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    public AjaxResult(){}

    /**
     * 初始化一个新创建的 AjaxResult 对象
     * 
     * @param code 状态码
     * @param msg 返回内容
     */
    public AjaxResult(int code, String msg)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     * 
     * @param code 状态码
     * @param msg 返回内容
     * @param data 数据对象
     */
    public AjaxResult(int code, String msg, Object data)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (data != null)
        {
            super.put(DATA_TAG, data);
        }
    }

    /**
     * 返回成功消息
     * 
     * @return 成功消息
     */
    public static AjaxResult success()
    {
        AjaxResultCode success = AjaxResultCode.SUCCESS;
        return AjaxResult.success(success.getMsg());
    }

    /**
     * 返回成功数据
     * 
     * @return 成功消息
     */
    public static AjaxResult success(Object data)
    {
        AjaxResultCode success = AjaxResultCode.SUCCESS;
        return AjaxResult.success(success.getMsg(), data);
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static AjaxResult success(AjaxResultCode success)
    {

        return AjaxResult.success(success.getMsg(), new HashMap<>(0));
    }

    /**
     * 返回成功消息
     * 
     * @param msg 返回内容
     * @return 成功消息
     */
    public static AjaxResult success(String msg)
    {
        return AjaxResult.success(msg, new HashMap<>(0));
    }

    /**
     * 返回成功消息
     * 
     * @param msg 返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static AjaxResult success(String msg, Object data)
    {
        AjaxResultCode success = AjaxResultCode.SUCCESS;
        return new AjaxResult(success.getCode(), msg, data);
    }


    /**
     * 返回特定状态描述
     *
     * @param statusCode 特定的枚举结果
     * @param data 数据对象
     * @return 请求结果
     */
    public static AjaxResult success(StatusCode statusCode, Object data) {
        return new AjaxResult(statusCode.getCode(), statusCode.getMsg(), data);
    }


    /**
     * 返回错误消息
     *
     * @return 警告消息
     */
    public static AjaxResult error()
    {
        return AjaxResult.error("操作失败");
    }

    /**
     * 返回错误消息
     * 
     * @param msg 返回内容
     * @return 警告消息
     */
    public static AjaxResult error(String msg)
    {
        return AjaxResult.error(msg, new HashMap<>(0));
    }

    /**
     * 返回错误消息
     * 
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static AjaxResult error(String msg, Object data)
    {
        AjaxResultCode loseEfficacy = AjaxResultCode.LOSE_EFFICACY;
        return new AjaxResult(loseEfficacy.getCode(), msg, data);
    }

    /**
     * 返回错误消息
     * 
     * @param code 状态码
     * @param msg 返回内容
     * @return 警告消息
     */
    public static AjaxResult error(int code, String msg)
    {
        return new AjaxResult(code, msg, new HashMap<>(0));
    }


    /**
     * 返回特定状态描述
     *
     * @param statusCode 特定的枚举结果
     * @param data 数据对象
     * @return 请求结果
     */
    public static AjaxResult error(StatusCode statusCode, Object data) {
        return new AjaxResult(statusCode.getCode(), statusCode.getMsg(), data);
    }


    /**
     * 返回特定状态描述
     *
     * @param statusCode 特定的枚举结果
     * @return 请求结果
     */
    public static AjaxResult error(StatusCode statusCode) {
        return new AjaxResult(statusCode.getCode(), statusCode.getMsg(), new HashMap<>(0));
    }
}
