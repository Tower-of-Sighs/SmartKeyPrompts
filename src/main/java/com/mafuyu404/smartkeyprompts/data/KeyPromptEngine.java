package com.mafuyu404.smartkeyprompts.data;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.init.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
public class KeyPromptEngine {
    private static final Map<String, Serializable> compiledExpressions = new HashMap<>();
    private static ParserContext parserContext = new ParserContext();
    private static final Map<String, Method> registeredFunctions = new HashMap<>();
    private static boolean functionsRegistered = false;

    static {
        registerFunctions();
    }

    /**
     * 自动扫描并注册带有 @SKPFunction 注解的方法
     */
    public static void registerFunctions() {
        try {
            // 清空现有注册
            parserContext = new ParserContext();
            registeredFunctions.clear();
            compiledExpressions.clear();

            // 扫描Utils类的所有方法
            Method[] methods = Utils.class.getDeclaredMethods();

            for (Method method : methods) {
                SKPFunction annotation = method.getAnnotation(SKPFunction.class);
                if (annotation != null) {
                    // 检查方法是否为静态方法
                    if (!Modifier.isStatic(method.getModifiers())) {
                        SmartKeyPrompts.LOGGER.warn("Warning: @SKPFunction method must be static: {}", method.getName());
                        continue;
                    }

                    // 获取函数名称
                    String functionName = annotation.value().isEmpty() ? method.getName() : annotation.value();

                    // 注册到 MVEL
                    parserContext.addImport(functionName, method);
                    registeredFunctions.put(functionName, method);

                    SmartKeyPrompts.LOGGER.info("Registered MVEL function: {}{}", functionName, annotation.description().isEmpty() ? "" : " - " + annotation.description());
                }
            }

            functionsRegistered = true;
            SmartKeyPrompts.LOGGER.info("Total registered MVEL functions: {}", registeredFunctions.size());

        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error("Error registering MVEL functions: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 热更新函数注册
     */
    public static void hotReloadFunctions() {
        SmartKeyPrompts.LOGGER.info("Hot reloading MVEL functions...");
        registerFunctions();
        SmartKeyPrompts.LOGGER.info("MVEL functions hot reload completed.");
    }

    /**
     * 获取已注册的函数列表
     */
    public static Map<String, Method> getRegisteredFunctions() {
        return new HashMap<>(registeredFunctions);
    }

    private static String currentModId;

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.screen != null || mc.getConnection() == null) return;

        Utils.setCurrentPlayer(player);

        Optional<Registry<KeyPromptData>> optionalRegistry =
                mc.getConnection().registryAccess().registry(KeyPromptDatapack.KEY_PROMPT_REGISTRY);

        if (optionalRegistry.isEmpty()) {
            return;
        }

        Registry<KeyPromptData> registry = optionalRegistry.get();

        for (KeyPromptData data : registry) {
            processKeyPromptData(data, player);
        }
    }

    private static void processKeyPromptData(KeyPromptData data, Player player) {
        currentModId = data.modid();

        Map<String, Object> context = createContext(data.vars(), player, data.modid());

        for (KeyPromptData.Entry entry : data.entries()) {
            if (checkConditions(entry.when(), context)) {
                executeActions(entry.then(), context);
            }
        }
    }

    private static Map<String, Object> createContext(Map<String, String> vars, Player player, String modid) {
        Map<String, Object> context = new HashMap<>();

        // 添加基础变量
        context.put("modid", modid);
        context.put("player", player);
        context.put("mainHandItem", Utils.getMainHandItemId());
        context.put("vehicleType", Utils.getVehicleType());
        context.put("targetedEntity", Utils.getTargetedEntityType());
        context.put("isInVehicle", player.getVehicle() != null);
        context.put("isSneaking", player.isShiftKeyDown());
        context.put("isSwimming", player.isSwimming());
        context.put("isFlying", player.getAbilities().flying);

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
                // 尝试作为表达式评估
                expectedValue = evaluateExpression(expression, context, false);
            } catch (Exception e) {
                // 如果评估失败，则当作字面值处理
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
            // 确保函数已注册
            if (!functionsRegistered) {
                registerFunctions();
            }

            // 使用缓存的编译表达式
            Serializable compiled = compiledExpressions.computeIfAbsent(expression,
                    expr -> MVEL.compileExpression(expr, parserContext));

            return MVEL.executeExpression(compiled, context);
        } catch (Exception e) {
            if (logErrors) {
                e.printStackTrace();
            }
            throw e;
        }
    }

    // 热更新函数
    @SKPFunction(value = "reload", description = "热更新MVEL函数")
    public static void reloadMVELFunctions() {
        hotReloadFunctions();
    }
}