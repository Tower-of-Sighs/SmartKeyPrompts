package com.mafuyu404.smartkeyprompts.util;

import com.mafuyu404.smartkeyprompts.init.KeyPrompt;

public class PromptUtils {
    public static void show(String id, String desc) {
        addDesc(desc).toGroup(id);
    }

    public static void custom(String id, String key, String desc) {
        addDesc(desc).forKey(key).withCustom(true).toGroup(id);
    }

    public static void alias(String id, String desc, String alias) {
        addDesc(desc).withKeyAlias(alias).toGroup(id);
    }

    public static KeyPrompt addDesc(String desc) {
        return new KeyPrompt("", KeyUtils.getKeyByDesc(desc), desc, false);
    }
}