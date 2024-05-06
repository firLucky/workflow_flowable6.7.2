package com.fir.flow.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author l'amour solitaire
 * @Description TODO
 * @Date 2020/8/11 下午8:55
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializeField {
 
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