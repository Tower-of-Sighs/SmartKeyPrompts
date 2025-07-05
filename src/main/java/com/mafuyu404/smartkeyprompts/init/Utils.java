package com.mafuyu404.smartkeyprompts.init;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.data.SKPFunction;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fml.ModList;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static Player currentPlayer;

    public static void setCurrentPlayer(Player player) {
        currentPlayer = player;
    }

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

    public static Entity getTargetedEntity() {
        Minecraft mc = Minecraft.getInstance();
        HitResult hit = mc.hitResult;
        if (hit instanceof EntityHitResult entityHit) {
            return entityHit.getEntity();
        }
        return null;
    }

    public static String getTargetedEntityType() {
        var entity = getTargetedEntity();
        return entity != null ? toPathString(entity.getType().toString()) : null;
    }

    public static ArrayList<HUD.KeyBindingInfo> getAllKeyBindings() {
        Minecraft mc = Minecraft.getInstance();
        ArrayList<HUD.KeyBindingInfo> bindingList = new ArrayList<>();
        for (KeyMapping binding : mc.options.keyMappings) {
            HUD.KeyBindingInfo info = new HUD.KeyBindingInfo(
                    "",
                    binding.getKey().getName(),
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
        if (text.contains("key.keyboard")) {
            text = text.split("\\.")[2].toUpperCase();
        }
        return text;
    }

    public static boolean isKeyPressed(int glfwKeyCode) {
        Minecraft minecraft = Minecraft.getInstance();
        long windowHandle = minecraft.getWindow().getWindow();
        return GLFW.glfwGetKey(windowHandle, glfwKeyCode) == GLFW.GLFW_PRESS;
    }

    @SKPFunction(description = "检查指定按键是否被按下")
    public static boolean isKeyPressedOfDesc(String key) {
        boolean result = false;
        for (KeyMapping keyMapping : Minecraft.getInstance().options.keyMappings) {
            if (key.equals(keyMapping.getName()) && isKeyPressed(keyMapping.getKey().getValue())) {
                result = true;
            }
        }
        return result;
    }

    @SKPFunction(description = "根据描述获取按键名称")
    public static String getKeyByDesc(String desc) {
        for (HUD.KeyBindingInfo keyBindingInfo : getAllKeyBindings()) {
            if (keyBindingInfo.desc().equals(desc)) return keyBindingInfo.key();
        }
        return "key.keyboard.unknown";
    }

    @SKPFunction(description = "获取Shift键名称")
    public static String getKeyShift() {
        return Minecraft.getInstance().options.keyShift.getKey().getName();
    }

    @SKPFunction(description = "获取使用键名称")
    public static String getKeyUse() {
        return Minecraft.getInstance().options.keyUse.getKey().getName();
    }

    @SKPFunction(description = "获取跳跃键名称")
    public static String getKeyJump() {
        return Minecraft.getInstance().options.keyJump.getKey().getName();
    }

    @SKPFunction(description = "获取背包键名称")
    public static String getKeyInventory() {
        return Minecraft.getInstance().options.keyInventory.getKey().getName();
    }

    @SKPFunction(description = "检查玩家是否拥有指定物品")
    public static boolean hasItem(String itemId) {
        if (currentPlayer == null) return false;
        return currentPlayer.getInventory().items.stream()
                .anyMatch(stack -> toPathString(stack.getItem().getDescriptionId()).equals(itemId));
    }

    @SKPFunction(description = "检查玩家是否在载具中")
    public static boolean isInVehicle() {
        return currentPlayer != null && currentPlayer.getVehicle() != null;
    }

    @SKPFunction(description = "获取目标实体类型")
    public static String getTargetType() {
        return getTargetedEntityType();
    }

    @SKPFunction(description = "显示按键提示")
    public static void show(String modid, String desc) {
        SmartKeyPrompts.show(modid, desc);
    }

    @SKPFunction(description = "显示自定义按键提示")
    public static void custom(String modid, String key, String desc) {
        SmartKeyPrompts.custom(modid, key, desc);
    }

    @SKPFunction(description = "显示按键别名提示")
    public static void alias(String modid, String key, String desc) {
        SmartKeyPrompts.alias(modid, key, desc);
    }

    @SKPFunction(description = "获取当前时间戳")
    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    @SKPFunction(description = "检查玩家是否在创造模式")
    public static boolean isCreativeMode() {
        return currentPlayer != null && currentPlayer.getAbilities().instabuild;
    }

    @SKPFunction(description = "检查目标实体是否为指定类型")
    public static boolean isTargetedEntityType(String entityType) {
        String targetType = getTargetedEntityType();
        return targetType != null && targetType.equals(entityType);
    }

    @SKPFunction(description = "检查摄像机实体是否为玩家")
    public static boolean isCameraPlayer() {
        return Minecraft.getInstance().getCameraEntity() instanceof Player;
    }

    @SKPFunction(description = "检查游戏界面是否打开")
    public static boolean isScreenOpen() {
        return Minecraft.getInstance().screen != null;
    }

    @SKPFunction(description = "检查指定mod是否已加载")
    public static boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }

}