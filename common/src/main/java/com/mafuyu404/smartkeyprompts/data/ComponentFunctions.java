package com.mafuyu404.smartkeyprompts.data;

import cc.sighs.oelib.data.api.ExpressionFunction;
import com.mafuyu404.smartkeyprompts.util.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/** Supplies item component expressions on Minecraft 1.21.1. */
public final class ComponentFunctions {
    @ExpressionFunction(description = "检查主手物品是否包含指定数据组件")
    public static boolean hasMainHandComponent(String componentId) {
        ItemStack itemStack = DataComponentUtils.getMainHandItem(DataPackFunctions.player());
        return DataComponentUtils.hasComponent(itemStack, componentId);
    }

    @ExpressionFunction(description = "检查主手物品数据组件的值是否匹配")
    public static boolean checkMainHandComponent(String componentId, String expectedValue) {
        ItemStack itemStack = DataComponentUtils.getMainHandItem(DataPackFunctions.player());
        return DataComponentUtils.checkComponentValue(itemStack, componentId, expectedValue);
    }

    @ExpressionFunction(description = "获取主手物品数据组件的值")
    public static String getMainHandComponentValue(String componentId) {
        ItemStack itemStack = DataComponentUtils.getMainHandItem(DataPackFunctions.player());
        return DataComponentUtils.getComponentValue(itemStack, componentId);
    }

    @ExpressionFunction(description = "检查主手物品自定义数据是否包含指定键")
    public static boolean hasMainHandCustomDataKey(String key) {
        ItemStack itemStack = DataComponentUtils.getMainHandItem(DataPackFunctions.player());
        return DataComponentUtils.hasCustomDataKey(itemStack, key);
    }

    @ExpressionFunction(description = "获取主手物品的完整数据组件信息")
    public static String getMainHandComponents() {
        ItemStack itemStack = DataComponentUtils.getMainHandItem(DataPackFunctions.player());
        return DataComponentUtils.getComponentsAsString(itemStack);
    }

    @ExpressionFunction(description = "获取主手物品的自定义数据字符串")
    public static String getMainHandCustomDataString() {
        ItemStack itemStack = DataComponentUtils.getMainHandItem(DataPackFunctions.player());
        return DataComponentUtils.getCustomDataAsString(itemStack);
    }

}
