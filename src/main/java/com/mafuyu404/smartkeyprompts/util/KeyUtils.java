package com.mafuyu404.smartkeyprompts.util;

import com.mafuyu404.smartkeyprompts.init.KeyPrompt;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class KeyUtils {
    /**
     * 获取所有按键绑定
     */
    public static ArrayList<KeyPrompt> getAllKeyBindings() {
        Minecraft mc = Minecraft.getInstance();
        ArrayList<KeyPrompt> bindingList = new ArrayList<>();
        for (KeyMapping binding : mc.options.keyMappings) {
            KeyPrompt info = new KeyPrompt(
                    "",
                    binding.getKey().getName(),
                    binding.getName(),
                    false
            );
            bindingList.add(info);
        }
        return bindingList;
    }

    /**
     * 翻译按键名称
     */
    public static String translateKey(String key) {
        if (key.contains("+")) {
            StringBuilder result = new StringBuilder();
            List.of(key.split("\\+")).forEach(part -> {
                if (!result.isEmpty()) result.append("+");
                result.append(translateKey(part));
            });
            return result.toString();
        }
        String text = Component.translatable(key).getString();
        if (text.contains("key.keyboard")) {
            text = text.split("\\.")[2].toUpperCase();
        }
        if (text.contains("key.mouse")) {
            text = Component.translatable("key.mouse.button").getString() + text.split("\\.")[2].toUpperCase();
        }
        return text;
    }

    /**
     * 检查按键是否被按下
     */
    public static boolean isKeyPressed(int glfwKeyCode) {
        Minecraft minecraft = Minecraft.getInstance();
        long windowHandle = minecraft.getWindow().getWindow();
        if (windowHandle == 0L) return false; // 窗口未准备好

        // GLFW 是否初始化且有效
        try {
            return GLFW.glfwGetKey(windowHandle, glfwKeyCode) == GLFW.GLFW_PRESS;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据描述检查按键是否被按下
     */
    public static boolean isKeyPressedOfDesc(String key) {
        for (KeyMapping keyMapping : Minecraft.getInstance().options.keyMappings) {
            if (key.equals(keyMapping.getName()) && keyMapping.isDown()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据描述获取按键名称
     */
    public static String getKeyByDesc(String desc) {
        for (KeyPrompt keyPrompt : getAllKeyBindings()) {
            if (keyPrompt.desc.equals(desc)) return keyPrompt.key;
        }
        return "key.keyboard.unknown";
    }

    /**
     * 检查按键是否为右键相关
     */
    public static boolean isRightClickKey(String keyDesc) {
        String keyName = getKeyByDesc(keyDesc);
        return keyName.equals("key.mouse.right") || keyName.equals("key.use");
    }

    /**
     * 通过物理按键名称检查按键是否被按下（支持组合键）
     */
    public static boolean isPhysicalKeyPressed(String keyName) {
        if (keyName == null || keyName.isEmpty()) {
            return false;
        }

        // 处理组合键
        if (keyName.contains("+")) {
            String[] keys = keyName.split("\\+");
            for (String key : keys) {
                if (!isPhysicalKeyPressed(key.trim())) {
                    return false;
                }
            }
            return true;
        }

        // 处理单个按键
        return isPhysicalKeySinglePressed(keyName);
    }

    /**
     * 检查单个物理按键是否被按下
     */
    private static boolean isPhysicalKeySinglePressed(String keyName) {
        if (keyName == null || keyName.isEmpty()) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        long windowHandle = minecraft.getWindow().getWindow();
        if (windowHandle == 0L) return false;

        try {
            Integer glfwKey = KEY_MAP.get(keyName);
            if (glfwKey == null) {
                return false;
            }

            if (keyName.startsWith("key.mouse.")) {
                return GLFW.glfwGetMouseButton(windowHandle, glfwKey) == GLFW.GLFW_PRESS;
            }

            if (keyName.startsWith("key.keyboard.")) {
                return GLFW.glfwGetKey(windowHandle, glfwKey) == GLFW.GLFW_PRESS;
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static final Collection<String> DisabledKeyMappingList = new HashSet<>();
    public static boolean isKeyDisabled(String desc) {
        return DisabledKeyMappingList.contains(desc);
    }
    public static Collection<String> getDisabledKeyMappingList() {
        return DisabledKeyMappingList;
    };
    public static void disableKeyMapping(String desc) {
        DisabledKeyMappingList.add(desc);
    }
    public static void disableKeyMapping(List<String> list) {
        DisabledKeyMappingList.addAll(list);
    }
    public static void disableAllKeyMapping() {
        getAllKeyBindings().forEach(keyPrompt -> disableKeyMapping(keyPrompt.desc));
    }
    public static void enableKeyMapping(String desc) {
        DisabledKeyMappingList.remove(desc);
    }
    public static void enableKeyMapping(List<String> list) {
        list.forEach(KeyUtils::enableKeyMapping);
    }
    public static void enableAllKeyMapping() {
        DisabledKeyMappingList.clear();
    }

    private static final HashMap<String, Integer> KEY_MAP = new HashMap<>();
    public static void addKeyMap(String key, int code) {
        KEY_MAP.put(key, code);
    }
    public static InputConstants.Key unknownKey;

    /**
     * 获取按键的翻译文本（不带任何前缀）
     */
    public static String getKeyDisplayName(String keyDesc) {
        String keyName = getKeyByDesc(keyDesc);
        return translateKey(keyName);
    }

    /**
     * 获取多个按键的翻译文本，用+连接
     */
    public static String getKeysDisplayName(String... keyDescs) {
        if (keyDescs == null || keyDescs.length == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < keyDescs.length; i++) {
            if (i > 0) result.append("+");
            result.append(getKeyDisplayName(keyDescs[i]));
        }
        return result.toString();
    }
}
