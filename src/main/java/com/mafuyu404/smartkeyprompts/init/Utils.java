package com.mafuyu404.smartkeyprompts.init;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static String getVehicleType(Player player) {
        if (player.getVehicle() == null) return null;
        return toPathString(player.getVehicle().getType().toString());
    }

    public static String getMainHandItemId(Player player) {
        return toPathString(player.getMainHandItem().getItem().getDescriptionId());
    }

    public static String toPathString(String key) {
        String[] path = key.split("\\.");
        return path[1] + ":" + path[2];
    }

    public static String getKeyByDesc(String desc) {
        for (HUD.KeyBindingInfo keyBindingInfo : getAllKeyBindings()) {
            if (keyBindingInfo.desc().equals(desc)) return keyBindingInfo.key();
        }
        return null;
    }

    public static Entity getTargetedEntity() {
        Minecraft mc = Minecraft.getInstance();
        HitResult hit = mc.hitResult;
        if (hit instanceof EntityHitResult entityHit) {
            return entityHit.getEntity();
        }
        return null;
    }

    public static ArrayList<HUD.KeyBindingInfo> getAllKeyBindings() {
        Minecraft mc = Minecraft.getInstance();
        ArrayList<HUD.KeyBindingInfo> bindingList = new ArrayList<>();
        for (KeyMapping binding : mc.options.keyMappings) {
            HUD.KeyBindingInfo info = new HUD.KeyBindingInfo(
                    "",
                    binding.key.getName(),
                    binding.getName(),
                    false
            );
            bindingList.add(info);
        }
        return bindingList;
    }

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

        // 处理键盘按键
        if (text.contains("key.keyboard")) {
            text = text.split("\\.")[2].toUpperCase();
        }
        // 处理鼠标按键
        else if (key.startsWith("key.mouse.")) {
            switch (key) {
                case "key.mouse.0" -> text = "左键";
                case "key.mouse.1" -> text = "右键";
                case "key.mouse.2" -> text = "中键";
                default -> {
                    // 对于其他鼠标按键，提取数字部分
                    String[] parts = key.split("\\.");
                    if (parts.length >= 3) {
                        text = "鼠标" + parts[2];
                    }
                }
            }
        }
        // 处理其他特殊按键
        else if (key.equals("key.mouse.wheel")) {
            text = "滚轮";
        }

        return text;
    }

    public static boolean isKeyPressed(int glfwKeyCode) {
        Minecraft minecraft = Minecraft.getInstance();
        long windowHandle = minecraft.getWindow().getWindow();
        return GLFW.glfwGetKey(windowHandle, glfwKeyCode) == GLFW.GLFW_PRESS;
    }

    public static boolean isKeyPressedOfDesc(String key) {
        boolean result = false;
        for (KeyMapping keyMapping : Minecraft.getInstance().options.keyMappings) {
            if (key.equals(keyMapping.getName()) && isKeyPressed(keyMapping.key.getValue())) {
                result = true;
            }
        }
        return result;
    }
}
