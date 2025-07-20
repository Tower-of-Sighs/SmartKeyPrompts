package com.mafuyu404.smartkeyprompts.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.data.KeyPromptData;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GsonUtils {

    private static final Gson GSON_INSTANCE = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocationTypeAdapter())
            .create();

    public static final Type KEY_PROMPT_DATA_MAP_TYPE = new TypeToken<Map<ResourceLocation, KeyPromptData>>() {
    }.getType();

    public static Gson getGson() {
        return GSON_INSTANCE;
    }


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

    private static class ResourceLocationTypeAdapter extends TypeAdapter<ResourceLocation> {
        @Override
        public void write(JsonWriter out, ResourceLocation value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString());
            }
        }

        @Override
        public ResourceLocation read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String value = in.nextString();
            try {
                return new ResourceLocation(value);
            } catch (Exception e) {
                SmartKeyPrompts.LOGGER.warn("Failed to parse ResourceLocation from string: {}", value);
                if (!value.contains(":")) {
                    return new ResourceLocation("smartkeyprompts", value);
                }
                throw new IOException("Invalid ResourceLocation format: " + value, e);
            }
        }
    }
}