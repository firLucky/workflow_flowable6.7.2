package com.fir.flow.config.result;

/**
 * 返回值枚举接口层
 * @author 18714
 */
public interface StatusCode {


    /**
     * 获取code信息
     *
     * @return code码
     */
     int getCode();


    /**
     * 获取msg信息
     *
     * @return msg描述
     */
     String getMsg();
}