package com.mafuyu404.smartkeyprompts.data;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.api.FunctionRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionUsageAnalyzer {

    private static final Pattern FUNCTION_PATTERN = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(");

    /**
     * 分析数据包中使用的所有函数
     */
    public static Set<String> analyzeUsedFunctions(Map<ResourceLocation, KeyPromptData> dataPackData) {
        Set<String> usedFunctions = new HashSet<>();

        for (Map.Entry<ResourceLocation, KeyPromptData> entry : dataPackData.entrySet()) {
            KeyPromptData data = entry.getValue();

            // 检查模组加载状态
            if (!shouldLoadDataPackFunctions(data, entry.getKey())) {
                continue;
            }

            Set<String> fileFunctions = analyzeSingleFile(data);
            usedFunctions.addAll(fileFunctions);
        }

        // 过滤出实际存在的函数
        Set<String> availableFunctions = FunctionRegistry.getAllFunctions().keySet();
        usedFunctions.retainAll(availableFunctions);

        SmartKeyPrompts.LOGGER.debug("Found {} used functions out of {} available functions",
                usedFunctions.size(), availableFunctions.size());

        return usedFunctions;
    }

    private static boolean shouldLoadDataPackFunctions(KeyPromptData data, ResourceLocation location) {
        if (data.vars() == null) {
            return true;
        }

        String modLoadedExpression = data.vars().get("modLoaded");
        if (modLoadedExpression == null) {
            return true;
        }

        try {
            String modId = extractModIdFromExpression(modLoadedExpression);
            if (modId != null) {
                boolean isLoaded = DataPackFunctions.isModLoaded(modId);
                SmartKeyPrompts.LOGGER.debug("Mod '{}' loaded status: {} for datapack {}", modId, isLoaded, location);
                return isLoaded;
            }
        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.debug("Failed to evaluate modLoaded expression for {}: {}", location, e.getMessage());
        }

        return true;
    }

    /**
     * 提取模组ID
     */
    private static String extractModIdFromExpression(String expression) {
        if (expression == null) return null;

        Pattern pattern = Pattern.compile("isModLoaded\\s*\\(\\s*['\"]([^'\"]+)['\"]\\s*\\)");
        Matcher matcher = pattern.matcher(expression);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    /**
     * 分析单个数据包文件中使用的函数
     */
    private static Set<String> analyzeSingleFile(KeyPromptData data) {
        Set<String> functions = new HashSet<>();

        // 分析变量定义中的函数
        if (data.vars() != null) {
            for (String expression : data.vars().values()) {
                functions.addAll(extractFunctionsFromExpression(expression));
            }
        }

        // 分析条件和动作中的函数
        if (data.entries() != null) {
            for (KeyPromptData.Entry entry : data.entries()) {
                // 条件
                if (entry.when() != null) {
                    for (String condition : entry.when().values()) {
                        functions.addAll(extractFunctionsFromExpression(condition));
                    }
                }

                // 动作
                if (entry.then() != null) {
                    for (String action : entry.then()) {
                        functions.addAll(extractFunctionsFromExpression(action));
                    }
                }
            }
        }

        return functions;
    }

    /**
     * 从表达式中提取函数名
     */
    private static Set<String> extractFunctionsFromExpression(String expression) {
        Set<String> functions = new HashSet<>();

        if (expression == null || expression.trim().isEmpty()) {
            return functions;
        }

        Matcher matcher = FUNCTION_PATTERN.matcher(expression);
        while (matcher.find()) {
            String functionName = matcher.group(1);

            if (!isKeyword(functionName)) {
                functions.add(functionName);
            }
        }

        return functions;
    }

    /**
     * 检查是否为关键字（非函数名）
     */
    private static boolean isKeyword(String word) {
        Set<String> keywords = Set.of(
                "if", "else", "for", "while", "do", "switch", "case", "default",
                "try", "catch", "finally", "throw", "throws", "return", "break", "continue",
                "new", "this", "super", "null", "true", "false", "instanceof",
                "public", "private", "protected", "static", "final", "abstract",
                "class", "interface", "extends", "implements", "package", "import",
                "int", "long", "float", "double", "boolean", "char", "byte", "short",
                "void", "String", "Object", "List", "Map", "Set"
        );

        return keywords.contains(word);
    }

    /**
     * 必需的核心函数
     */
    public static Set<String> getCoreRequiredFunctions() {
        return Set.of("isModLoaded");
    }
}