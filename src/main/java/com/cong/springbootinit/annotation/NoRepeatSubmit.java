package com.cong.springbootinit.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoRepeatSubmit {
    /**
     * 防重复提交的过期时间，单位：秒
     */
    int expireTime() default 5;
}
