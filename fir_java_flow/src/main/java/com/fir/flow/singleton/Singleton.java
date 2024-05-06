package com.fir.flow.singleton;


import com.fir.flow.utils.TimerExpireHashMap;

/**
 * 双检锁/双重校验锁（DCL，即 double-checked locking) 单例模式
 * @author fir
 */
public class Singleton {

    public String fileUrl;

    /**
     * token请求头参数名称
     */
    public final String TOKEN_HEADER_NAME = "Authorization";

    /**
     * 定时存储map(临时取代redis，不能用生产环境)
     */
    public TimerExpireHashMap<String, Object> timerExpireHashMap = new TimerExpireHashMap<>();


    private volatile static Singleton singleton;  
    private Singleton (){}  
    public static Singleton getSingleton() {  
    if (singleton == null) {  
        synchronized (Singleton.class) {  
            if (singleton == null) {  
                singleton = new Singleton();  
            }  
        }  
    }  
    return singleton;  
    }  
}