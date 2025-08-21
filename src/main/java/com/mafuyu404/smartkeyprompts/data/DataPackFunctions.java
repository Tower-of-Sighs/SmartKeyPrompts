package com.mafuyu404.smartkeyprompts.data;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.api.SKPFunction;
import com.mafuyu404.smartkeyprompts.init.Utils;
import com.mafuyu404.smartkeyprompts.utils.DataComponentUtils;
import com.mafuyu404.smartkeyprompts.utils.NBTUtils;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

/**
 * 用于数据包MVEL表达式的函数类
 * 所有带@SKPFunction注解的方法都应该放在这里
 */
public class DataPackFunctions {
    @Setter
    private static Player currentPlayer;

    @SKPFunction(description = "检查指定按键是否被按下")
    public static boolean isKeyPressedOfDesc(String key) {
        return Utils.isKeyPressedOfDesc(key);
    }

    @SKPFunction(description = "根据描述获取按键名称")
    public static String getKeyByDesc(String desc) {
        return Utils.getKeyByDesc(desc);
    }

    @SKPFunction(description = "检查玩家是否拥有指定物品")
    public static boolean hasItem(String itemId) {
        if (currentPlayer == null) return false;
        return currentPlayer.getInventory().items.stream()
                .anyMatch(stack -> Utils.toPathString(stack.getItem().getDescriptionId()).equals(itemId));
    }

    @SKPFunction(description = "检查玩家是否在载具中")
    public static boolean isInVehicle() {
        return currentPlayer != null && currentPlayer.getVehicle() != null;
    }

    @SKPFunction(description = "获取目标实体类型")
    public static String getTargetType() {
        return Utils.getTargetedEntityType();
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
        String targetType = Utils.getTargetedEntityType();
        return targetType != null && targetType.equals(entityType);
    }

    @SKPFunction(description = "是否存在目标实体")
    public static boolean hasTargetEntity() {
        return Utils.getTargetedEntity() != null;
    }

    @SKPFunction(description = "检查摄像机实体是否为玩家")
    public static boolean isCameraPlayer() {
        return Minecraft.getInstance().getCameraEntity() instanceof Player;
    }

    @SKPFunction(description = "检查游戏界面是否打开")
    public static boolean isScreenOpen() {
        return Minecraft.getInstance().screen != null;
    }

    @SKPFunction(description = "检查主手物品是否包含指定数据组件")
    public static boolean hasMainHandComponent(String componentId) {
        ItemStack itemStack = DataComponentUtils.getMainHandItem(currentPlayer);
        return DataComponentUtils.hasComponent(itemStack, componentId);
    }

    @SKPFunction(description = "检查主手物品数据组件的值是否匹配")
    public static boolean checkMainHandComponent(String componentId, String expectedValue) {
        ItemStack itemStack = DataComponentUtils.getMainHandItem(currentPlayer);
        return DataComponentUtils.checkComponentValue(itemStack, componentId, expectedValue);
    }

    @SKPFunction(description = "获取主手物品数据组件的值")
    public static String getMainHandComponentValue(String componentId) {
        ItemStack itemStack = DataComponentUtils.getMainHandItem(currentPlayer);
        return DataComponentUtils.getComponentValue(itemStack, componentId);
    }

    @SKPFunction(description = "检查主手物品自定义数据是否包含指定键")
    public static boolean hasMainHandCustomDataKey(String key) {
        ItemStack itemStack = DataComponentUtils.getMainHandItem(currentPlayer);
        return DataComponentUtils.hasCustomDataKey(itemStack, key);
    }

    @SKPFunction(description = "获取主手物品的完整数据组件信息")
    public static String getMainHandComponents() {
        ItemStack itemStack = DataComponentUtils.getMainHandItem(currentPlayer);
        return DataComponentUtils.getComponentsAsString(itemStack);
    }

    @SKPFunction(description = "获取主手物品的自定义数据字符串")
    public static String getMainHandCustomDataString() {
        ItemStack itemStack = DataComponentUtils.getMainHandItem(currentPlayer);
        return DataComponentUtils.getCustomDataAsString(itemStack);
    }

    @SKPFunction(description = "检查目标实体是否包含指定NBT路径")
    public static boolean hasTargetEntityNBT(String nbtPath) {
        CompoundTag nbt = NBTUtils.getTargetEntityNBT();
        return NBTUtils.checkNBTPath(nbt, nbtPath);
    }

    @SKPFunction(description = "检查目标实体NBT路径的值是否匹配")
    public static boolean checkTargetEntityNBT(String nbtPath, String expectedValue) {
        CompoundTag nbt = NBTUtils.getTargetEntityNBT();
        return NBTUtils.checkNBTValue(nbt, nbtPath, expectedValue);
    }

    @SKPFunction(description = "获取目标实体NBT路径的值")
    public static String getTargetEntityNBTValue(String nbtPath) {
        CompoundTag nbt = NBTUtils.getTargetEntityNBT();
        return NBTUtils.getNBTValue(nbt, nbtPath);
    }

    @SKPFunction(description = "获取目标实体的完整SNBT")
    public static String getTargetEntitySNBT() {
        CompoundTag nbt = NBTUtils.getTargetEntityNBT();
        return NBTUtils.getNBTAsString(nbt);
    }

    @SKPFunction(description = "检查目标方块实体是否包含指定NBT路径")
    public static boolean hasTargetBlockEntityNBT(String nbtPath) {
        CompoundTag nbt = NBTUtils.getTargetBlockEntityNBT();
        return NBTUtils.checkNBTPath(nbt, nbtPath);
    }

    @SKPFunction(description = "检查目标方块实体NBT路径的值是否匹配")
    public static boolean checkTargetBlockEntityNBT(String nbtPath, String expectedValue) {
        CompoundTag nbt = NBTUtils.getTargetBlockEntityNBT();
        return NBTUtils.checkNBTValue(nbt, nbtPath, expectedValue);
    }

    @SKPFunction(description = "检查指定mod是否已加载")
    public static boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }

    @SKPFunction(value = "reload", description = "热更新MVEL函数")
    public static void reloadMVELFunctions() {
        KeyPromptEngine.hotReloadFunctions();
    }
}