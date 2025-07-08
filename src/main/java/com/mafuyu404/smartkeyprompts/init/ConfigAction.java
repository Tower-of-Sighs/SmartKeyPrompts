package com.mafuyu404.smartkeyprompts.init;

import com.mafuyu404.smartkeyprompts.Config;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = SmartKeyPrompts.MOD_ID)
public class ConfigAction {
    @SubscribeEvent
    public static void mouseAction(InputEvent.MouseButton.Pre event) {
        if (event.getAction() != InputConstants.PRESS) return;
        if (Minecraft.getInstance().player == null) return;
        if (!Utils.isKeyPressed(ModKeybindings.CONTROL_KEY.getKey().getValue())) return;
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            List<String> currentKey = HUD.KeyPromptList.stream()
                    .filter(keyBindingInfo -> !keyBindingInfo.isCustom() && Utils.getKeyByDesc(keyBindingInfo.getDesc()) != null)
                    .map(KeyPrompt::getDesc).toList();
            modifyKey(currentKey);
        }
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            int position = Config.POSITION.get();
            if (position == 8) Config.POSITION.set(1);
            else Config.POSITION.set(position + 1);
            Config.POSITION.save();
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void wheelAction(InputEvent.MouseScrollingEvent event) {
        if (Minecraft.getInstance().player == null) return;
        if (!Utils.isKeyPressed(ModKeybindings.CONTROL_KEY.getKey().getValue())) return;
        scaleHUD(event.getScrollDeltaY());
        event.setCanceled(true);
    }

    public static void scaleHUD(double delta) {
        double scale = Config.SCALE.get();
        if (delta < 0) {
            scale = Math.max(scale - 0.1, 0);
        } else {
            scale = Math.min(scale + 0.1, 10);
        }
        Config.SCALE.set(scale);
        Config.SCALE.save();
    }

    public static void modifyKey(List<String> keyNames) {
        if (HUD.KeyMappingCache != null) return;

        Minecraft minecraft = Minecraft.getInstance();

        HUD.KeyMappingCache = minecraft.options.keyMappings;

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
