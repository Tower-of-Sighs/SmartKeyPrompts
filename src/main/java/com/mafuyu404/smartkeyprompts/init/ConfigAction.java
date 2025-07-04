package com.mafuyu404.smartkeyprompts.init;

import com.mafuyu404.smartkeyprompts.ModConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mafuyu404.smartkeyprompts.init.HUD.KeyMappingCache;


public class ConfigAction {
    private static boolean mousePressed = false;
    private static double lastScrollTime = 0;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            long window = client.getWindow().getWindow();

            if (GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
                if (!mousePressed && Utils.isKeyPressed(ModKeybindings.CONTROL_KEY.key.getValue())) {
                    mousePressed = true;
                    handleLeftClick();
                }
            } else {
                mousePressed = false;
            }

            if (GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS) {
                if (Utils.isKeyPressed(ModKeybindings.CONTROL_KEY.key.getValue())) {
                    handleRightClick();
                }
            }
        });

        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenMouseEvents.allowMouseScroll(screen).register((screenInstance, mouseX, mouseY, horizontalAmount, verticalAmount) -> {
                if (client.player == null) return true;
                if (!Utils.isKeyPressed(ModKeybindings.CONTROL_KEY.key.getValue())) return true;

                // 防止重复触发
                double currentTime = System.currentTimeMillis();
                if (currentTime - lastScrollTime < 100) return false;
                lastScrollTime = currentTime;

                scaleHUD(verticalAmount);
                return false;
            });
        });
    }

    private static void handleLeftClick() {
        List<String> currentKey = HUD.bindingInfoList.stream()
                .filter(keyBindingInfo -> !keyBindingInfo.custom() && Utils.getKeyByDesc(keyBindingInfo.desc()) != null)
                .map(HUD.KeyBindingInfo::desc).toList();
        modifyKey(currentKey);
    }

    private static void handleRightClick() {
        ModConfig config = ModConfig.getInstance();
        if (config.position == 8) {
            config.position = 1;
        } else {
            config.position += 1;
        }
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
    }

    public static void modifyKey(List<String> keyNames) {
        if (KeyMappingCache != null) return;

        Minecraft minecraft = Minecraft.getInstance();

        KeyMappingCache = minecraft.options.keyMappings;

        KeyMapping[] allKeys = minecraft.options.keyMappings;
        Set<String> targetKeys = keyNames.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        KeyMapping[] filteredKeys = new KeyMapping[allKeys.length];
        int count = 0;

        for (KeyMapping key : allKeys) {
            String name = key.getName().toLowerCase();
            if (targetKeys.contains(name)) {
                filteredKeys[count++] = key;
            }
        }

        KeyMapping[] tempKeys = new KeyMapping[count];
        System.arraycopy(filteredKeys, 0, tempKeys, 0, count);

        minecraft.options.keyMappings = tempKeys;
        minecraft.setScreen(new KeyBindsScreen(null, minecraft.options));
    }
}