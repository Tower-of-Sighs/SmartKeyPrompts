package com.mafuyu404.smartkeyprompts.init;

import com.mafuyu404.smartkeyprompts.Config;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mafuyu404.smartkeyprompts.init.HUD.KeyMappingCache;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
public class ConfigAction {

    @SubscribeEvent
    public static void mouseAction(InputEvent.MouseButton.Pre event) {
        if (event.getAction() != InputConstants.PRESS) return;
        if (Minecraft.getInstance().player == null) return;
        if (!Utils.isKeyPressed(ModKeybindings.CONTROL_KEY.getKey().getValue())) return;

        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            List<String> currentKey = getCurrentKeyDescs();
            modifyKey(currentKey);
        }
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            int position = Config.POSITION.get();
            if (position == 8) {
                Config.POSITION.set(1);
            } else {
                Config.POSITION.set(position + 1);
            }
            Config.POSITION.save();
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void wheelAction(InputEvent.MouseScrollingEvent event) {
        if (Minecraft.getInstance().player == null) return;
        if (!Utils.isKeyPressed(ModKeybindings.CONTROL_KEY.getKey().getValue())) return;
        scaleHUD(event.getScrollDelta());
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

    private static List<String> getCurrentKeyDescs() {
        List<String> currentKey = new ArrayList<>();
        synchronized (HUD.KeyPromptList) {
            for (KeyPrompt keyPrompt : HUD.KeyPromptList) {
                if (keyPrompt != null && !keyPrompt.isCustom && Utils.getKeyByDesc(keyPrompt.desc) != null) {
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