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
     * 分区键
     */
    String partitionKey() default "";

    /**
     * 分片数量
     */
    int shard() default 0;


}
