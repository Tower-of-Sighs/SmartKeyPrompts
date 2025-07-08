package com.mafuyu404.smartkeyprompts.api;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MVEL 函数注册管理器。
 * <p>
 * 负责管理和注册所有带有 {@link SKPFunction} 注解的方法到 MVEL 表达式引擎中。
 * 支持多模组函数注册，提供函数名冲突检测和热重载功能。
 * </p>
 *
 * <h3>工作流程：</h3>
 * <ol>
 *   <li>触发 {@link FunctionRegistryEvent} 事件</li>
 *   <li>收集所有模组注册的函数类</li>
 *   <li>扫描类中的 {@code @SKPFunction} 方法</li>
 *   <li>验证方法签名和函数名唯一性</li>
 *   <li>注册到 MVEL 表达式引擎</li>
 * </ol>
 *
 * @author Flechazo
 * @see SKPFunction
 * @see FunctionRegistryEvent
 * @since 1.0.0
 */
public class FunctionRegistry {

    /**
     * 已注册的函数类集合。
     * 使用线程安全的 Set 来存储已注册的类。
     */
    private static final Set<Class<?>> registeredClasses = ConcurrentHashMap.newKeySet();

    /**
     * 函数名到方法的映射表。
     * 存储所有已注册的函数，键为函数名，值为对应的方法对象。
     */
    private static final Map<String, Method> functionMap = new ConcurrentHashMap<>();
    /**
     * 初始化状态标志。
     * 标记注册器是否已经完成初始化。
     */
    private static boolean initialized = false;

    /**
     * 注册包含 {@link SKPFunction} 注解方法的类。
     * <p>
     * 如果注册器已经初始化，会立即扫描新注册的类。
     * 否则会在 {@link #initialize()} 调用时统一扫描。
     * </p>
     *
     * @param clazz 要注册的类，不能为 {@code null}
     * @param modid 模组ID，用于日志记录，不能为 {@code null}
     * @throws NullPointerException 如果参数为 null
     */
    public static void registerFunctionClass(Class<?> clazz, String modid) {
        if (clazz == null) {
            throw new NullPointerException("Function class cannot be null");
        }
        if (modid == null) {
            throw new NullPointerException("Mod ID cannot be null");
        }

        registeredClasses.add(clazz);
        if (initialized) {
            scanClass(clazz, modid);
        }
    }

    /**
     * 初始化函数注册器。
     * <p>
     * 此方法会：
     * <ol>
     *   <li>清空现有的函数映射</li>
     *   <li>触发 {@link FunctionRegistryEvent} 事件</li>
     *   <li>扫描所有已注册的函数类</li>
     *   <li>记录注册结果</li>
     * </ol>
     * </p>
     *
     * @apiNote 此方法通常由 SmartKeyPrompts 内部调用，模组开发者无需手动调用
     */
    public static void initialize() {
        functionMap.clear();

        // 触发函数注册事件
        FunctionRegistryEvent event = new FunctionRegistryEvent();
        NeoForge.EVENT_BUS.post(event);

        // 注册事件中收集的函数类
        for (Pair<Class<?>, String> entry : event.getRegisteredClasses()) {
            registerFunctionClass(entry.getLeft(), entry.getRight());
        }

        // 扫描所有已注册的类
        for (Class<?> clazz : registeredClasses) {
            scanClass(clazz, "unknown");
        }

        initialized = true;
        SmartKeyPrompts.LOGGER.info("MVEL 函数注册完成，总数：{}", functionMap.size());
    }

    /**
     * 扫描指定类中的 {@link SKPFunction} 注解方法。
     * <p>
     * 扫描过程中会验证：
     * <ul>
     *   <li>方法必须是静态的</li>
     *   <li>函数名必须全局唯一</li>
     * </ul>
     * 不符合要求的方法会被跳过并记录警告日志。
     * </p>
     *
     * @param clazz 要扫描的类
     * @param modid 模组ID，用于日志记录
     */
    private static void scanClass(Class<?> clazz, String modid) {
        for (Method method : clazz.getDeclaredMethods()) {
            SKPFunction ann = method.getAnnotation(SKPFunction.class);
            if (ann == null) continue;

            // 验证方法必须是静态的
            if (!Modifier.isStatic(method.getModifiers())) {
                SmartKeyPrompts.LOGGER.warn("函数必须是静态的: {}.{}", clazz.getSimpleName(), method.getName());
                continue;
            }

            // 获取函数名
            String name = ann.value().isEmpty() ? method.getName() : ann.value();

            // 检查函数名冲突
            if (functionMap.containsKey(name)) {
                Method conflict = functionMap.get(name);
                SmartKeyPrompts.LOGGER.warn("函数名冲突: {} 与 {}.{} 冲突", name, conflict.getDeclaringClass().getSimpleName(), conflict.getName());
                continue;
            }

            functionMap.put(name, method);
            SmartKeyPrompts.LOGGER.debug("注册函数：{} ({})", name, clazz.getSimpleName());
        }
    }

    /**
     * 获取所有已注册的函数映射。
     * <p>
     * 返回函数名到方法对象的映射副本，确保线程安全。
     * </p>
     *
     * @return 函数映射的副本，键为函数名，值为方法对象，不会为 {@code null}
     */
    public static Map<String, Method> getAllFunctions() {
        return new HashMap<>(functionMap);
    }

    /**
     * 清空所有已注册的函数和类。
     * <p>
     * 此方法主要用于热重载功能，会重置注册器到初始状态。
     * </p>
     *
     * @apiNote 此方法通常由 SmartKeyPrompts 内部调用，模组开发者无需手动调用
     */
    public static void clear() {
        functionMap.clear();
        registeredClasses.clear();
        initialized = false;
    }
}