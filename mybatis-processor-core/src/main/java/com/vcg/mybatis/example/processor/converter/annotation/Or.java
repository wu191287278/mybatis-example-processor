package com.vcg.mybatis.example.processor.converter.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Or {

    /**
     * 字段名称.
     */
    String[] value();

}
