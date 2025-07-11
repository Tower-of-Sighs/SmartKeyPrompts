package com.mafuyu404.smartkeyprompts.init;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    /**
     * 获取载具类型
     */
    public static String getVehicleType() {
        Player player = Minecraft.getInstance().player;
        if (player.getVehicle() == null) return null;
        return toPathString(player.getVehicle().getType().toString());
    }

    /**
     * 获取主手物品ID
     */
    public static String getMainHandItemId() {
        return toPathString(Minecraft.getInstance().player.getMainHandItem().getItem().getDescriptionId());
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
        for (KeyPrompt keyPrompt : getAllKeyBindings()) {
            if (keyPrompt.desc.equals(desc)) return keyPrompt.key;
        }
        return "key.keyboard.unknown";
    }

    /**
     * 检查玩家是否在移动
     */
    public static boolean isPlayerMoving() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return false;
        return player.getDeltaMovement().horizontalDistanceSqr() > 0.01;
    }

    /**
     * 检查玩家是否在空中
     */
    public static boolean isPlayerInAir() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return false;
        return !player.onGround();
    }

    /**
     * 检查玩家是否在地面
     */
    public static boolean isPlayerOnGround() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return false;
        return player.onGround();
    }

    /**
     * 检查是否有敌对目标实体
     */
    public static boolean hasTargetedEntityIsMob() {
        Entity entity = getTargetedEntity();
        if (entity == null) return false;

        return entity instanceof Enemy ||
                (entity instanceof Mob mob && mob.getTarget() instanceof Player);
    }
}