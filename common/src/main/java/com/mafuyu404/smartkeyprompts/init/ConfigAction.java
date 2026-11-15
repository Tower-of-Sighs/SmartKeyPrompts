package com.mafuyu404.smartkeyprompts.init;

import com.mafuyu404.smartkeyprompts.platform.ClientPlatform;
import com.mafuyu404.smartkeyprompts.Config;
import com.mafuyu404.smartkeyprompts.client.KeyGuideScreen;
import com.mafuyu404.smartkeyprompts.client.KeyGuideClient;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.util.KeyUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mafuyu404.smartkeyprompts.init.HUD.KeyMappingCache;

public class ConfigAction {

    public static boolean mouseAction(int button, int action) {
        if (action != InputConstants.PRESS) return false;
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().screen instanceof KeyGuideScreen) return false;
        if (!KeyUtils.isKeyPressed(ClientPlatform.get().key(ClientPlatform.get().controlKey()).getValue())) return false;
        if (!KeyGuideClient.getBindingIds().isEmpty()) {
            return button == GLFW.GLFW_MOUSE_BUTTON_LEFT && KeyGuideClient.openEditor();
        }

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            List<String> currentKey = getCurrentKeyDescs();
            modifyKey(currentKey);
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            int position = Config.get().position();
            if (position == 8) {
                ClientPlatform.get().setPosition(1);
            } else {
                ClientPlatform.get().setPosition(position + 1);
            }
        }
        return true;
    }

    public static boolean wheelAction(double scrollDeltaY) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().screen instanceof KeyGuideScreen) return false;
        if (!KeyGuideClient.getBindingIds().isEmpty()) return false;
        if (!KeyUtils.isKeyPressed(ClientPlatform.get().key(ClientPlatform.get().controlKey()).getValue())) return false;
        scaleHUD(scrollDeltaY);
        return true;
    }

    public static void scaleHUD(double delta) {
        if (!KeyGuideClient.getBindingIds().isEmpty()) return;
        double scale = Config.get().scale();
        if (delta < 0) {
            scale = Math.max(scale - 0.1, 0);
        } else {
            scale = Math.min(scale + 0.1, 10);
        }
        ClientPlatform.get().setScale(scale);
    }

    private static List<String> getCurrentKeyDescs() {
        List<String> currentKey = new ArrayList<>();
        synchronized (HUD.KeyPromptList) {
            for (KeyPrompt keyPrompt : HUD.KeyPromptList) {
                if (keyPrompt != null && !keyPrompt.isCustom && KeyUtils.getKeyByDesc(keyPrompt.desc) != null) {
                    currentKey.add(keyPrompt.desc);
                }
            }
        }
        return currentKey;
    }

    public static void modifyKey(List<String> keyNames) {
        if (KeyMappingCache != null) return;
        if (keyNames == null || keyNames.isEmpty()) return;

        Minecraft minecraft = Minecraft.getInstance();
        KeyMappingCache = minecraft.options.keyMappings;

        KeyMapping[] allKeys = minecraft.options.keyMappings;

        Set<String> targetKeys = new HashSet<>();
        for (String keyName : keyNames) {
            if (keyName != null) {
                targetKeys.add(keyName.toLowerCase());
            }
        }

        List<KeyMapping> filteredKeysList = new ArrayList<>();
        for (KeyMapping key : allKeys) {
            if (key != null && targetKeys.contains(key.getName().toLowerCase())) {
                filteredKeysList.add(key);
            }
        }

        minecraft.options.keyMappings = filteredKeysList.toArray(new KeyMapping[0]);
        ClientPlatform.get().openKeyBindings();
    }
}
