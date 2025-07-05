package com.mafuyu404.smartkeyprompts.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记需要注册到 MVEL 表达式引擎的函数
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SKPFunction {
    /**
     * 函数在 MVEL 中的名称，如果为空则使用方法名
     */
    String value() default "";

    /**
     * 函数描述
     */
    String description() default "";
}