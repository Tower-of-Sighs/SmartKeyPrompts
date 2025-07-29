package com.mafuyu404.smartkeyprompts.data;

import com.mafuyu404.oelib.api.ExpressionFunction;
import com.mafuyu404.smartkeyprompts.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/**
 * 用于数据包MVEL表达式的函数类
 * 所有带@ExpressionFunction注解的方法都应该放在这里
 */
public class DataPackFunctions {
    private static Player currentPlayer;

    public static void setCurrentPlayer(Player player) {
        currentPlayer = player;
    }

    // ========== 基础变量函数 ==========

    @ExpressionFunction(description = "获取当前玩家", category = "mod")
    public static Player player() {
        return currentPlayer;
    }

    @ExpressionFunction(description = "获取主手物品ID", category = "mod")
    public static String mainHandItem() {
        return PlayerUtils.getMainHandItemId();
    }

    @ExpressionFunction(description = "获取载具类型", category = "mod")
    public static String vehicleType() {
        return PlayerUtils.getVehicleType();
    }

    @ExpressionFunction(description = "获取目标实体类型", category = "mod")
    public static String targetedEntity() {
        return PlayerUtils.getTargetedEntityType();
    }

    @ExpressionFunction(description = "检查玩家是否在载具中", category = "mod")
    public static boolean isInVehicle() {
        return currentPlayer != null && currentPlayer.getVehicle() != null;
    }

    @ExpressionFunction(description = "检查玩家是否在游泳", category = "mod")
    public static boolean isSwimming() {
        return currentPlayer != null && currentPlayer.isSwimming();
    }

    @ExpressionFunction(description = "检查玩家是否在飞行", category = "mod")
    public static boolean isFlying() {
        return currentPlayer != null && currentPlayer.getAbilities().flying;
    }

    @ExpressionFunction(description = "获取玩家生命值百分比", category = "mod")
    public static float healthPercentage() {
        return PlayerUtils.getHealthPercentage();
    }

    @ExpressionFunction(description = "获取玩家饥饿值", category = "mod")
    public static int foodLevel() {
        return PlayerUtils.getFoodLevel();
    }

    @ExpressionFunction(description = "获取玩家经验等级", category = "mod")
    public static int experienceLevel() {
        return PlayerUtils.getExperienceLevel();
    }

    // ========== 按键相关函数 ==========

    @ExpressionFunction(description = "根据描述获取按键名称", category = "mod")
    public static String getKeyByDesc(String desc) {
        return KeyUtils.getKeyByDesc(desc);
    }

    @ExpressionFunction(description = "检查指定按键是否被按下", category = "mod")
    public static boolean isKeyPressedOfDesc(String key) {
        return KeyUtils.isKeyPressedOfDesc(key);
    }

    // ========== 实体和环境检查函数 ==========

    @ExpressionFunction(description = "检查目标实体是否为指定类型", category = "mod")
    public static boolean isTargetedEntityType(String entityType) {
        String targetType = PlayerUtils.getTargetedEntityType();
        return targetType != null && targetType.equals(entityType);
    }

    @ExpressionFunction(description = "是否存在目标实体", category = "mod")
    public static boolean hasTargetEntity() {
        return PlayerUtils.getTargetedEntity() != null;
    }

    @ExpressionFunction(description = "检查摄像机实体是否为玩家", category = "mod")
    public static boolean isCameraPlayer() {
        return Minecraft.getInstance().getCameraEntity() instanceof Player;
    }

    @ExpressionFunction(description = "检查游戏界面是否打开", category = "mod")
    public static boolean isScreenOpen() {
        return CommonUtils.isScreenOpen();
    }

    @ExpressionFunction(description = "检查玩家是否在创造模式", category = "mod")
    public static boolean isCreativeMode() {
        return currentPlayer != null && currentPlayer.getAbilities().instabuild;
    }

    // ========== 物品和NBT相关函数 ==========

    @ExpressionFunction(description = "检查玩家是否拥有指定物品", category = "mod")
    public static boolean hasItem(String itemId) {
        if (currentPlayer == null) return false;
        return currentPlayer.getInventory().items.stream()
                .anyMatch(stack -> CommonUtils.toPathString(stack.getItem().getDescriptionId()).equals(itemId));
    }

    @ExpressionFunction(description = "检查主手物品是否包含指定NBT路径", category = "mod")
    public static boolean hasMainHandNBT(String nbtPath) {
        CompoundTag nbt = NBTUtils.getMainHandNBT(currentPlayer);
        return NBTUtils.checkNBTPath(nbt, nbtPath);
    }

    @ExpressionFunction(description = "检查主手物品NBT路径的值是否匹配", category = "mod")
    public static boolean checkMainHandNBT(String nbtPath, String expectedValue) {
        CompoundTag nbt = NBTUtils.getMainHandNBT(currentPlayer);
        return NBTUtils.checkNBTValue(nbt, nbtPath, expectedValue);
    }

    @ExpressionFunction(description = "获取主手物品NBT路径的值", category = "mod")
    public static String getMainHandNBTValue(String nbtPath) {
        CompoundTag nbt = NBTUtils.getMainHandNBT(currentPlayer);
        return NBTUtils.getNBTValue(nbt, nbtPath);
    }

    @ExpressionFunction(description = "检查主手物品NBT是否匹配SNBT格式", category = "mod")
    public static boolean matchMainHandSNBT(String snbt) {
        CompoundTag nbt = NBTUtils.getMainHandNBT(currentPlayer);
        return NBTUtils.matchSNBT(nbt, snbt);
    }

    @ExpressionFunction(description = "获取主手物品的完整SNBT", category = "mod")
    public static String getMainHandSNBT() {
        CompoundTag nbt = NBTUtils.getMainHandNBT(currentPlayer);
        return NBTUtils.getNBTAsString(nbt);
    }

    // ========== 目标实体NBT函数 ==========

    @ExpressionFunction(description = "检查目标实体是否包含指定NBT路径", category = "mod")
    public static boolean hasTargetEntityNBT(String nbtPath) {
        CompoundTag nbt = NBTUtils.getTargetEntityNBT();
        return NBTUtils.checkNBTPath(nbt, nbtPath);
    }

    @ExpressionFunction(description = "检查目标实体NBT路径的值是否匹配", category = "mod")
    public static boolean checkTargetEntityNBT(String nbtPath, String expectedValue) {
        CompoundTag nbt = NBTUtils.getTargetEntityNBT();
        return NBTUtils.checkNBTValue(nbt, nbtPath, expectedValue);
    }

    @ExpressionFunction(description = "获取目标实体NBT路径的值", category = "mod")
    public static String getTargetEntityNBTValue(String nbtPath) {
        CompoundTag nbt = NBTUtils.getTargetEntityNBT();
        return NBTUtils.getNBTValue(nbt, nbtPath);
    }

    @ExpressionFunction(description = "获取目标实体的完整SNBT", category = "mod")
    public static String getTargetEntitySNBT() {
        CompoundTag nbt = NBTUtils.getTargetEntityNBT();
        return NBTUtils.getNBTAsString(nbt);
    }

    // ========== 目标方块实体NBT函数 ==========

    @ExpressionFunction(description = "检查目标方块实体是否包含指定NBT路径", category = "mod")
    public static boolean hasTargetBlockEntityNBT(String nbtPath) {
        CompoundTag nbt = NBTUtils.getTargetBlockEntityNBT();
        return NBTUtils.checkNBTPath(nbt, nbtPath);
    }

    @ExpressionFunction(description = "检查目标方块实体NBT路径的值是否匹配", category = "mod")
    public static boolean checkTargetBlockEntityNBT(String nbtPath, String expectedValue) {
        CompoundTag nbt = NBTUtils.getTargetBlockEntityNBT();
        return NBTUtils.checkNBTValue(nbt, nbtPath, expectedValue);
    }

    @ExpressionFunction(description = "获取目标实体NBT路径的值", category = "mod")
    public static String getTargetBlockEntityNBTValue(String nbtPath) {
        CompoundTag nbt = NBTUtils.getTargetBlockEntityNBT();
        return NBTUtils.getNBTValue(nbt, nbtPath);
    }

    @ExpressionFunction(description = "获取目标实体的完整SNBT", category = "mod")
    public static String getTargetBlockEntitySNBT() {
        CompoundTag nbt = NBTUtils.getTargetBlockEntityNBT();
        return NBTUtils.getNBTAsString(nbt);
    }

    // ========== 系统函数 ==========

    @ExpressionFunction(description = "获取当前时间戳", category = "mod")
    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    // ========== 显示函数 ==========

    @ExpressionFunction(description = "显示按键提示", category = "mod")
    public static void show(String modid, String desc) {
        PromptUtils.show(modid, desc);
    }

    @ExpressionFunction(description = "显示自定义按键提示", category = "mod")
    public static void custom(String modid, String key, String desc) {
        PromptUtils.custom(modid, key, desc);
    }

    @ExpressionFunction(description = "显示按键别名提示", category = "mod")
    public static void alias(String modid, String key, String desc) {
        PromptUtils.alias(modid, key, desc);
    }
}