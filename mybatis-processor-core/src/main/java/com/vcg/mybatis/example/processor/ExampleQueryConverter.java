package com.vcg.mybatis.example.processor;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ExampleQueryConverter {

    /**
     * example 包路径.
     */
    String example();

    /**
     * 自定义模版路径.
     */
    String template() default "templates/Converter.java";

}
