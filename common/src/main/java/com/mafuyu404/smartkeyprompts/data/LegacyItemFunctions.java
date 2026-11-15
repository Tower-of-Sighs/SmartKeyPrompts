package com.mafuyu404.smartkeyprompts.data;

import cc.sighs.oelib.data.api.ExpressionFunction;
import com.mafuyu404.smartkeyprompts.util.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/** Supplies item NBT expressions on Minecraft 1.20.1. */
public final class LegacyItemFunctions {
    @ExpressionFunction(description = "检查主手物品是否包含指定NBT路径", category = "mod")
    public static boolean hasMainHandNBT(String nbtPath) {
        CompoundTag nbt = NBTUtils.getMainHandNBT(DataPackFunctions.player());
        return NBTUtils.checkNBTPath(nbt, nbtPath);
    }

    @ExpressionFunction(description = "检查主手物品NBT路径的值是否匹配", category = "mod")
    public static boolean checkMainHandNBT(String nbtPath, String expectedValue) {
        CompoundTag nbt = NBTUtils.getMainHandNBT(DataPackFunctions.player());
        return NBTUtils.checkNBTValue(nbt, nbtPath, expectedValue);
    }

    @ExpressionFunction(description = "获取主手物品NBT路径的值", category = "mod")
    public static String getMainHandNBTValue(String nbtPath) {
        CompoundTag nbt = NBTUtils.getMainHandNBT(DataPackFunctions.player());
        return NBTUtils.getNBTValue(nbt, nbtPath);
    }

    @ExpressionFunction(description = "检查主手物品NBT是否匹配SNBT格式", category = "mod")
    public static boolean matchMainHandSNBT(String snbt) {
        CompoundTag nbt = NBTUtils.getMainHandNBT(DataPackFunctions.player());
        return NBTUtils.matchSNBT(nbt, snbt);
    }

    @ExpressionFunction(description = "获取主手物品的完整SNBT", category = "mod")
    public static String getMainHandSNBT() {
        CompoundTag nbt = NBTUtils.getMainHandNBT(DataPackFunctions.player());
        return NBTUtils.getNBTAsString(nbt);
    }

}
