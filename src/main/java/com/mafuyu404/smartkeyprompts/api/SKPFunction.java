package com.mafuyu404.smartkeyprompts.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记需要注册到 MVEL 表达式引擎的函数注解。
 * <p>
 * 使用此注解标记的静态方法将被自动注册到 SmartKeyPrompts 的 MVEL 表达式引擎中，
 * 可以在数据包的条件表达式和动作表达式中使用。
 * </p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * @SKPFunction(value = "isHoldingItem", description = "检查玩家是否持有指定物品")
 * public static boolean isHoldingItem(String itemId) {
 *     // 实现逻辑
 *     return false;
 * }
 * }</pre>
 *
 * @author Flechazo
 * @see FunctionRegistry
 * @see FunctionRegistryEvent
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SKPFunction {

    /**
     * 函数在 MVEL 表达式中的名称。
     * <p>
     * 如果为空字符串，则使用方法名作为函数名。
     * 函数名必须在全局范围内唯一，重复的函数名会导致注册失败。
     * </p>
     *
     * @return 函数名称，默认为空字符串
     */
    String value() default "";

    /**
     * 函数的描述信息。
     * <p>
     * 用于文档生成和调试日志，建议提供清晰的功能描述。
     * </p>
     *
     * @return 函数描述，默认为空字符串
     */
    String description() default "";
}