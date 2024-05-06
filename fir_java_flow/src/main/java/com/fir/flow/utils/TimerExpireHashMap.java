package com.fir.flow.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Map's KEY is automatic expire
 * @author zhangshuai
 */
@SuppressWarnings("unchecked")
public class TimerExpireHashMap<K, V> extends HashMap<K, V> {
 
	private static final long serialVersionUID = -609074433772310225L;
 

    /** 定时删除过期键，间隔时间 **/
    private static final long CHECK_TIME_SECOND = 1000;

    /** 默认过期时间容器初始化数量 **/
    private static final int DEFAULT_INITIAL_CAPACITY = 8;
 

    /** 定时器 **/
    private final Timer timer = new Timer();

    /** 期时间容器 **/
    private Map<Object, Long> timerMap;

    /** 过期数据回调 **/
    @SuppressWarnings("rawtypes")
	private TimerExpireHashMapCallback timerExpireHashMapCallback ;
 
    /**
     * 定时删除过期键
     */
    private final TimerTask timerTask = new TimerTask() {
		@SuppressWarnings("unchecked")
		@Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            timerMap.keySet().removeIf(key -> {
            	Long keyTime = timerMap.get(key);
                if (currentTime >= keyTime) {
                    if(timerExpireHashMapCallback != null) {
                        try {
                            timerExpireHashMapCallback.callback((K)key, (V)TimerExpireHashMap.super.get(key));
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                    }
                    remove(key);
                    return true;
                }
                return false;
            });
        }
    };
 
    /**
     * 构造方法
     * @param initialCapacity   容器初始数量
     * @param loadFactor        随机因子
     */
    public TimerExpireHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        init(initialCapacity, loadFactor);
    }
 
    /**
     * 构造方法
     * @param initialCapacity   容器初始数量
     */
    public TimerExpireHashMap(int initialCapacity) {
        super(initialCapacity);
        init(initialCapacity, 10);
    }
 
    /**
     * 构造方法
     */
    public TimerExpireHashMap() {
        super();
        init(DEFAULT_INITIAL_CAPACITY, 	10);
    }
 
    /**
     * 构造方法
     * @param map
     */
    public TimerExpireHashMap(Map<K, V> map) {
        super(map);
        init(DEFAULT_INITIAL_CAPACITY, 10);
    }
 
    /**
     * 初始化过期时间容器
     * @param initialCapacity   容器初始数量
     * @param loadFactor        随机因子
     */
    private void init(int initialCapacity, float loadFactor) {
        timerMap = new HashMap<>(initialCapacity, loadFactor);
        timer.scheduleAtFixedRate(timerTask, 0, CHECK_TIME_SECOND);
    }
 
    /**
     * 获取数据
     * @param key 键值
     */
    @Override
    public V get(Object key) {
        Long expireTime = checkKeyExpireTime(key);
        if (expireTime == null || expireTime > 0) {
            return super.get(key);
        }
        return null;
    }


    /**
     * 删除数据
     * @param key key
     */
    @Override
    public V remove(Object key) {
        Long expireTime = checkKeyExpireTime(key);
        if (expireTime == null || expireTime > 0) {
            return super.remove(key);
        }
        return null;
    }
 
    /**
     * 放入数据
     * @param key           键值
     * @param value         数据
     * @param expireSecond  过期时间（秒）
     * @return  数据
     */
    public V put(K key, V value, Long expireSecond) {
        if(expireSecond != null && expireSecond.longValue() > 0) {
            setKeyExpireTime(key, expireSecond);
        }
        return super.put(key, value);
    }
 
    /**
     * 返回key过期剩余时间（秒）
     * @param key   键值
     * @return      返回key过期剩余时间（秒）
     */
    public Long checkKeyExpireTime(Object key) {
        Long second = timerMap.get(key);
        if(second == null) {
            return null;
        }
        long currentTime = System.currentTimeMillis();
        return ((second - currentTime) / 1000);
    }
 
    /**
     * 为键值设置过期时间
     * @param key               键值
     * @param expireSecond      过期时间（秒）
     */
    public void setKeyExpireTime(Object key, Long expireSecond) {
        /* && this.containsKey(key) */
		if (expireSecond != null && expireSecond > 0) {
            long currentTime = System.currentTimeMillis();
            long expireTime = currentTime + (expireSecond * 1000);
            timerMap.put(key, expireTime);
        }
    }
 
    /**
     * 设置过期数据设置监听
     * @param timerExpireHashMapCallback    监听回调
     */
    public void setTimerExpireHashMapCallback(TimerExpireHashMapCallback<K, V> timerExpireHashMapCallback) {
        this.timerExpireHashMapCallback = timerExpireHashMapCallback;
    }
 
    /**
     * 数据设置回调
     * @param <K>
     * @param <V>
     */
    static interface TimerExpireHashMapCallback<K, V> {
        /**
         * 监听回调
         * @param key   过期键
         * @param value 过期值
         */
        public void callback(K key, V value) throws RuntimeException;
    }
}
 
 
 
 

