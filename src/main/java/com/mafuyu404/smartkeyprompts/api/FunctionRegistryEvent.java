package com.mafuyu404.smartkeyprompts.api;

import net.neoforged.bus.api.Event;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * 函数注册事件。
 * <p>
 * 此事件在 SmartKeyPrompts 初始化 MVEL 函数注册时触发，
 * 其他模组可以监听此事件来注册自己的函数类。
 * </p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * @Mod.EventBusSubscriber(modid = "yourmod", value = Dist.CLIENT)
 * public class YourModSKPIntegration {
 *
 *     @SubscribeEvent(priority = EventPriority.HIGH)
 *     public static void onFunctionRegistration(FunctionRegistryEvent event) {
 *         event.registerFunctionClass(YourModFunctions.class, "yourmod");
 *     }
 * }
 * }</pre>
 *
 * @author Flechazo
 * @see FunctionRegistry
 * @see SKPFunction
 * @since 1.0.0
 */
public class FunctionRegistryEvent extends Event {

    /**
     * 已注册的函数类列表。
     * 存储类和对应的模组ID的配对。
     */
    private final List<Pair<Class<?>, String>> registered = new ArrayList<>();

    /**
     * 注册包含 {@link SKPFunction} 注解方法的类。
     * <p>
     * 注册的类中所有带有 {@code @SKPFunction} 注解的静态方法
     * 都会被扫描并注册到 MVEL 表达式引擎中。
     * </p>
     *
     * @param clazz 要注册的类，不能为 {@code null}
     * @param modid 模组ID，用于日志记录和调试，不能为 {@code null}
     * @throws NullPointerException 如果 clazz 或 modid 为 null
     */
    public void registerFunctionClass(Class<?> clazz, String modid) {
        if (clazz == null) {
            throw new NullPointerException("Function class cannot be null");
        }
        if (modid == null) {
            throw new NullPointerException("Mod ID cannot be null");
        }
        registered.add(Pair.of(clazz, modid));
    }

    /**
     * 获取所有已注册的函数类列表。
     * <p>
     * 返回的列表包含类和对应模组ID的配对。
     * 此方法主要供 {@link FunctionRegistry} 内部使用。
     * </p>
     *
     * @return 已注册的函数类列表，不会为 {@code null}
     */
    public List<Pair<Class<?>, String>> getRegisteredClasses() {
        return registered;
    }
}