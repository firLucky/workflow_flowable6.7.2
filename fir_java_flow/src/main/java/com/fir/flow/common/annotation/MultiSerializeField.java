package com.fir.flow.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author l'amour solitaire
 * @Description 用于嵌套类情况的二层过滤（本质目的，为了可以二次执行）
 * @Date 2020/8/11 下午8:52
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiSerializeField {
 
    Class clazz();
 
    /**
     * 需要返回的字段
     * @return
     */
    String[] includes() default {};
 
    /**
     * 需要去除的字段
     * @return
     */
    String[] excludes() default {};
}