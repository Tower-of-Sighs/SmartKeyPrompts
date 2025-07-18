package com.mafuyu404.smartkeyprompts.data;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.api.FunctionRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
public class KeyPromptEngine {
    private static final Map<String, Serializable> compiledExpressions = new HashMap<>();
    private static ParserContext parserContext = new ParserContext();
    private static final Map<String, Method> registeredFunctions = new HashMap<>();
    private static boolean functionsRegistered = false;
    private static Set<String> lastUsedFunctions = new HashSet<>();

    public static void registerFunctions() {
        Map<ResourceLocation, KeyPromptData> loadedData = KeyPromptDatapack.getLoadedData();
        registerFunctions(loadedData);
    }

     //根据数据包内容选择性注册函数
    public static void registerFunctions(Map<ResourceLocation, KeyPromptData> dataPackData) {
        try {
            parserContext = new ParserContext();
            registeredFunctions.clear();
            compiledExpressions.clear();

            FunctionRegistry.initialize();

            // 分析数据包中使用的函数
            Set<String> usedFunctions = new HashSet<>();
            if (dataPackData != null && !dataPackData.isEmpty()) {
                usedFunctions = FunctionUsageAnalyzer.analyzeUsedFunctions(dataPackData);
            }

            usedFunctions.addAll(FunctionUsageAnalyzer.getCoreRequiredFunctions());

            // 获取所有可用函数并注册使用到的函数
            Map<String, Method> allFunctions = FunctionRegistry.getAllFunctions();
            int registeredCount = 0;
            for (String functionName : usedFunctions) {
                Method method = allFunctions.get(functionName);
                if (method != null) {
                    parserContext.addImport(functionName, method);
                    registeredFunctions.put(functionName, method);
                    registeredCount++;
                }
            }

            lastUsedFunctions = new HashSet<>(usedFunctions);
            functionsRegistered = true;

            SmartKeyPrompts.LOGGER.info("Registered {} MVEL functions ({}% optimization)",
                    registeredCount, Math.round((1.0 - (double)registeredCount / allFunctions.size()) * 100));

        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error("Error registering MVEL functions: {}", e.getMessage(), e);
            fallbackRegisterAllFunctions();
        }
    }

    /**
     * 兜底方案：注册所有函数
     */
    private static void fallbackRegisterAllFunctions() {
        try {
            SmartKeyPrompts.LOGGER.warn("Falling back to registering all functions");

            parserContext = new ParserContext();
            registeredFunctions.clear();
            compiledExpressions.clear();

            Map<String, Method> allFunctions = FunctionRegistry.getAllFunctions();
            for (Map.Entry<String, Method> entry : allFunctions.entrySet()) {
                parserContext.addImport(entry.getKey(), entry.getValue());
                registeredFunctions.put(entry.getKey(), entry.getValue());
            }

            functionsRegistered = true;
            SmartKeyPrompts.LOGGER.info("Fallback: registered all {} MVEL functions", registeredFunctions.size());

        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error("Critical error: fallback function registration failed: {}", e.getMessage(), e);
        }
    }

    public static void hotReloadFunctions() {
        FunctionRegistry.clear();
        registerFunctions();
        SmartKeyPrompts.LOGGER.debug("MVEL functions hot reload completed");
    }

    public static void forceReload() {
        compiledExpressions.clear();
        hotReloadFunctions();
    }

    /**
     * 强制重载并传入数据包数据
     */
    public static void forceReloadWithData(Map<ResourceLocation, KeyPromptData> dataPackData) {
        compiledExpressions.clear();
        FunctionRegistry.clear();
        registerFunctions(dataPackData);
    }

    /**
     * 获取已注册的函数列表(调试)
     */
    public static Map<String, Method> getRegisteredFunctions() {
        return new HashMap<>(registeredFunctions);
    }

    /**
     * 获取上次使用的函数列表（调试）
     */
    public static Set<String> getLastUsedFunctions() {
        return new HashSet<>(lastUsedFunctions);
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        if (!functionsRegistered) {
            registerFunctions();
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.screen != null) return;

        DataPackFunctions.setCurrentPlayer(player);

        Map<ResourceLocation, KeyPromptData> loadedData = KeyPromptDatapack.getLoadedData();
        for (Map.Entry<ResourceLocation, KeyPromptData> entry : loadedData.entrySet()) {
            KeyPromptData data = entry.getValue();

            if (data.vars() != null && data.vars().containsKey("modLoaded")) {
                try {
                    Map<String, Object> tempContext = new HashMap<>();
                    Object modLoadedResult = evaluateExpression(data.vars().get("modLoaded"), tempContext, false);

                    if (Boolean.FALSE.equals(modLoadedResult)) {
                        continue;
                    }
                } catch (Exception e) {
                    SmartKeyPrompts.LOGGER.debug("Failed to evaluate modLoaded for {}: {}", entry.getKey(), e.getMessage());
                }
            }

            processKeyPromptData(data, player);
        }
    }

    private static void processKeyPromptData(KeyPromptData data, Player player) {
        Map<String, Object> context = createContext(data.vars());

        for (KeyPromptData.Entry entry : data.entries()) {
            if (checkConditions(entry.when(), context)) {
                executeActions(entry.then(), context);
            }
        }
    }

    private static Map<String, Object> createContext(Map<String, String> vars) {
        Map<String, Object> context = new HashMap<>();

        // 解析自定义变量
        for (Map.Entry<String, String> var : vars.entrySet()) {
            Object value = evaluateExpression(var.getValue(), context);
            context.put(var.getKey(), value);
        }

        return context;
    }

    private static boolean checkConditions(Map<String, String> conditions, Map<String, Object> context) {
        for (Map.Entry<String, String> condition : conditions.entrySet()) {
            String key = condition.getKey();
            String expression = condition.getValue();

            Object actualValue = context.get(key);
            Object expectedValue;

            try {
                expectedValue = evaluateExpression(expression, context, false);
            } catch (Exception e) {
                expectedValue = expression;
            }

            // 支持通配符匹配
            if (expectedValue instanceof String expectedStr && expectedStr.contains("*")) {
                String pattern = expectedStr.replace("*", ".*");
                if (actualValue == null || !actualValue.toString().matches(pattern)) {
                    return false;
                }
            } else if (!Objects.equals(expectedValue, actualValue)) {
                return false;
            }
        }
        return true;
    }

    private static void executeActions(List<String> actions, Map<String, Object> context) {
        for (String action : actions) {
            evaluateExpression(action, context);
        }
    }

    private static Object evaluateExpression(String expression, Map<String, Object> context) {
        return evaluateExpression(expression, context, true);
    }

    private static Object evaluateExpression(String expression, Map<String, Object> context, boolean logErrors) {
        try {
            if (!functionsRegistered) {
                registerFunctions();
            }

            Serializable compiled = compiledExpressions.computeIfAbsent(expression,
                    expr -> MVEL.compileExpression(expr, parserContext));

            return MVEL.executeExpression(compiled, context);
        } catch (Exception e) {
            if (logErrors) {
                SmartKeyPrompts.LOGGER.error("Failed to evaluate expression: {}", expression, e);
            }
            throw e;
        }
    }
}