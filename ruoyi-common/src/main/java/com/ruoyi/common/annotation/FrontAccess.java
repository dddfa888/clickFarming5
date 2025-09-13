package com.ruoyi.common.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 前台用户访问接口标识
 * 只允许 loginUser.getmUser() 类型的用户访问
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FrontAccess {
    String value() default "";
}
