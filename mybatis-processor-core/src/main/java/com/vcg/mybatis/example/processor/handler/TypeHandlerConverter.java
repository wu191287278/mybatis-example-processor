package com.vcg.mybatis.example.processor.handler;

import java.lang.annotation.*;

import org.apache.ibatis.type.TypeHandler;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface TypeHandlerConverter {

    /**
     * 自定义TypeHandler处理器.
     */
    Class<? extends TypeHandler> value();

}
