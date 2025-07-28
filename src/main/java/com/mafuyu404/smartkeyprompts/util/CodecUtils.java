package com.mafuyu404.smartkeyprompts.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.data.KeyPromptData;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodecUtils {

    public static final Codec<Map<ResourceLocation, KeyPromptData>> KEY_PROMPT_DATA_MAP_CODEC =
            Codec.unboundedMap(ResourceLocation.CODEC, KeyPromptData.CODEC);

    /**
     * 将数据序列化为 JSON 字符串
     */
    public static String encodeToJson(Map<ResourceLocation, KeyPromptData> data) {
        try {
            DataResult<JsonElement> result = KEY_PROMPT_DATA_MAP_CODEC.encodeStart(JsonOps.INSTANCE, data);
            if (result.error().isPresent()) {
                SmartKeyPrompts.LOGGER.error("Failed to encode data: {}", result.error().get().message());
                return null;
            }
            return result.result().orElse(null).toString();
        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error("Exception during encoding: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从 JSON 字符串反序列化数据
     */
    public static Map<ResourceLocation, KeyPromptData> decodeFromJson(String jsonData) {
        try {
            JsonElement jsonElement = com.google.gson.JsonParser.parseString(jsonData);
            DataResult<Map<ResourceLocation, KeyPromptData>> result =
                    KEY_PROMPT_DATA_MAP_CODEC.parse(JsonOps.INSTANCE, jsonElement);

            if (result.error().isPresent()) {
                SmartKeyPrompts.LOGGER.error("Failed to decode data: {}", result.error().get().message());
                return null;
            }
            return result.result().orElse(null);
        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error("Exception during decoding: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从 JsonElement 反序列化单个 KeyPromptData
     */
    public static KeyPromptData decodeKeyPromptData(JsonElement jsonElement) {
        try {
            DataResult<KeyPromptData> result = KeyPromptData.CODEC.parse(JsonOps.INSTANCE, jsonElement);
            if (result.error().isPresent()) {
                SmartKeyPrompts.LOGGER.error("Failed to decode KeyPromptData: {}", result.error().get().message());
                return null;
            }
            return result.result().orElse(null);
        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error("Exception during KeyPromptData decoding: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 分析 JSON 内容（调试用）
     */
    public static void analyzeJsonContent(String jsonData, String sessionId) {
        try {
            SmartKeyPrompts.LOGGER.error("Analyzing problematic JSON content for session {}", sessionId);

            String trimmed = jsonData.trim();
            if (!trimmed.startsWith("{")) {
                SmartKeyPrompts.LOGGER.error("JSON does not start with '{{' character. First 100 chars: {}",
                        trimmed.length() > 100 ? trimmed.substring(0, 100) : trimmed);
            }

            if (!trimmed.endsWith("}")) {
                SmartKeyPrompts.LOGGER.error("JSON does not end with '}}' character. Last 100 chars: {}",
                        trimmed.length() > 100 ? trimmed.substring(trimmed.length() - 100) : trimmed);
            }

            extractFileReferences(jsonData);

            if (jsonData.contains("\"modid\"")) {
                SmartKeyPrompts.LOGGER.error("JSON contains modid field, suggesting it's key prompt data");
            }

        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error("Error during JSON analysis: {}", e.getMessage());
        }
    }

    private static void extractFileReferences(String jsonData) {
        Pattern keyPattern = Pattern.compile("\"smartkeyprompts:key_prompts/([^\"]+)\"");
        Matcher matcher = keyPattern.matcher(jsonData);

        while (matcher.find()) {
            String fileName = matcher.group(1) + ".json";
            SmartKeyPrompts.LOGGER.error("JSON content references file: {}", fileName);
        }

        Pattern modidPattern = Pattern.compile("\"modid\"\\s*:\\s*\"([^\"]+)\"");
        Matcher modidMatcher = modidPattern.matcher(jsonData);

        while (modidMatcher.find()) {
            String modid = modidMatcher.group(1);
            SmartKeyPrompts.LOGGER.error("JSON contains modid: {}, possible file: {}.json", modid, modid);
        }
    }
}