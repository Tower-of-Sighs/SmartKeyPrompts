package com.mafuyu404.smartkeyprompts.init;

import com.mafuyu404.smartkeyprompts.ModConfig;
import com.mafuyu404.smartkeyprompts.util.KeyUtils;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mafuyu404.smartkeyprompts.init.HUD.KeyMappingCache;

public class ConfigAction {

    public static void init() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenMouseEvents.allowMouseClick(screen).register((screen1, mouseX, mouseY, button) -> {
                if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT && button != GLFW.GLFW_MOUSE_BUTTON_RIGHT) return true;
                if (Minecraft.getInstance().player == null) return true;
                if (!KeyUtils.isKeyPressed(ModKeybindings.CONTROL_KEY.key.getValue())) return true;

                if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    List<String> currentKey = getCurrentKeyDescs();
                    modifyKey(currentKey);
                }
                if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    int position = ModConfig.getInstance().position;
                    if (position == 8) {
                        ModConfig.getInstance().position = 1;
                    } else {
                        ModConfig.getInstance().position = position + 1;
                    }
                    AutoConfig.getConfigHolder(ModConfig.class).save();

                }
                return false;
            });

            ScreenMouseEvents.allowMouseScroll(screen).register((screen1, mouseX, mouseY, horizontalAmount, verticalAmount) -> {
                if (Minecraft.getInstance().player == null) return true;
                if (!KeyUtils.isKeyPressed(ModKeybindings.CONTROL_KEY.key.getValue())) return true;
                scaleHUD(verticalAmount);
                return false;
            });
        });
    }

    public static void scaleHUD(double delta) {
        ModConfig config = ModConfig.getInstance();
        double scale = config.scale;

        if (delta < 0) {
            scale = Math.max(scale - 0.1, 0);
        } else {
            scale = Math.min(scale + 0.1, 10);
        }

        config.scale = scale;
        AutoConfig.getConfigHolder(ModConfig.class).save();
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
        minecraft.setScreen(new KeyBindsScreen(null, minecraft.options));
    }
}