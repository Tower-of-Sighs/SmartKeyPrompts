package com.mafuyu404.smartkeyprompts.util;

import com.mafuyu404.smartkeyprompts.init.KeyPrompt;

import java.util.HashSet;

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

    private static final HashSet<String> disabledPromptByGroup = new HashSet<>();
    private static final HashSet<String> disabledPromptByDesc = new HashSet<>();
    private static final HashSet<String> disabledPrompt = new HashSet<>();

    public static void disablePromptByGroup(String group) {
        disabledPromptByGroup.add(group);
    }
    public static void enablePromptByGroup(String group) {
        disabledPromptByGroup.remove(group);
    }

    public static void disablePromptByDesc(String desc) {
        disabledPromptByDesc.add(desc);
    }
    public static void enablePromptByDesc(String desc) {
        disabledPromptByDesc.remove(desc);
    }

    public static void disablePrompt(String group, String desc) {
        disabledPrompt.add(group + ":" + desc);
    }
    public static void enablePrompt(String group, String desc) {
        disabledPrompt.remove(group + ":" + desc);
    }

    public static boolean checkPromptValid(KeyPrompt keyPrompt) {
        boolean checkGroup = disabledPromptByGroup.contains(keyPrompt.group);
        boolean checkDesc = disabledPromptByDesc.contains(keyPrompt.desc);
        boolean check = disabledPrompt.contains(keyPrompt.group + ":" + keyPrompt.desc) || checkGroup || checkDesc;
        return !check;
    }
}
