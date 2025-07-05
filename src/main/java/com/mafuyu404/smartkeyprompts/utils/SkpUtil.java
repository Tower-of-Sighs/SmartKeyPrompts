package com.mafuyu404.smartkeyprompts.utils;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;

public class SkpUtil {
    public static void show(String id, String desc) {
        SmartKeyPrompts.show(id, desc);
    }

    public static void custom(String id, String key, String desc) {
        SmartKeyPrompts.custom(id, key, desc);
    }

    public static void alias(String id, String key, String desc) {
        SmartKeyPrompts.alias(id, key, desc);
    }
}
