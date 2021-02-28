package com.vcg.mybatis.example.processor;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Example {

    /**
     * 命名空间.
     */
    String namespace();

    /**
     * 是否生成query对象.
     */
    boolean query() default false;

}
