package com.vcg.mybatis.example.processor.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GreaterThanOrEqualTo {

    /**
     * 字段名称.
     */
    String value() default "";

}
