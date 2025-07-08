package com.mafuyu404.smartkeyprompts.init;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.data.SKPFunction;
import lombok.Setter;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.fml.ModList;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    /**
     * 获取载具类型
     */
    public static String getVehicleType() {
        Player player = Minecraft.getInstance().player;
        if (player == null || player.getVehicle() == null) return null;
        return toPathString(player.getVehicle().getType().toString());
    }

    /**
     * 获取主手物品ID
     */
    public static String getMainHandItemId() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return null;
        return toPathString(player.getMainHandItem().getItem().getDescriptionId());
    }

    /**
     * 将描述ID转换为路径字符串格式
     */
    public static String toPathString(String key) {
        String[] path = key.split("\\.");
        return path[1] + ":" + path[2];
    }

    /**
     * 获取目标实体
     */
    public static Entity getTargetedEntity() {
        Minecraft mc = Minecraft.getInstance();
        HitResult hit = mc.hitResult;
        if (hit instanceof EntityHitResult entityHit) {
            return entityHit.getEntity();
        }
        return null;
    }

    /**
     * 获取目标方块
     */
    public static BlockState getTargetedBlock() {
        Minecraft mc = Minecraft.getInstance();
        HitResult hit = mc.hitResult;
        if (hit instanceof BlockHitResult targetedBlock) {
            return Minecraft.getInstance().player.level().getBlockState(targetedBlock.getBlockPos());
        }
        return null;
    }

    /**
     * 获取目标实体类型
     */
    public static String getTargetedEntityType() {
        var entity = getTargetedEntity();
        return entity != null ? toPathString(entity.getType().toString()) : null;
    }

    /**
     * 获取目标方块ID
     */
    public static String getTargetedBlockId() {
        BlockState blockState = getTargetedBlock();
        return blockState != null ? toPathString(blockState.getBlock().getDescriptionId()) : null;
    }

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
        return GLFW.glfwGetKey(windowHandle, glfwKeyCode) == GLFW.GLFW_PRESS;
    }

    /**
     * 根据描述检查按键是否被按下
     */
    public static boolean isKeyPressedOfDesc(String key) {
        boolean result = false;
        for (KeyMapping keyMapping : Minecraft.getInstance().options.keyMappings) {
            if (key.equals(keyMapping.getName()) && isKeyPressed(keyMapping.getKey().getValue())) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 根据描述获取按键名称
     */
    public static String getKeyByDesc(String desc) {
        for (KeyPrompt keyBindingInfo : getAllKeyBindings()) {
            if (keyBindingInfo.getDesc().equals(desc)) return keyBindingInfo.getKey();
        }
        return "key.keyboard.unknown";
    }
}
