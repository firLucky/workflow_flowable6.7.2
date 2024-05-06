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
public @interface MoreSerializeField {
    SerializeField[] value() default {};
}