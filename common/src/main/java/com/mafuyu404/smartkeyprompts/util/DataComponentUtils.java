package com.mafuyu404.smartkeyprompts.util;

import com.mafuyu404.smartkeyprompts.platform.ComponentItemData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Preserves the shared item-query API while the target supplies component access. */
public final class DataComponentUtils {
    private static final ComponentItemData DATA = new ComponentItemData();
    public static boolean hasComponent(ItemStack stack, String componentId) {
        return DATA.hasComponent(stack, componentId);
    }

    public static boolean checkComponentValue(ItemStack stack, String componentId, String expectedValue) {
        return DATA.checkComponentValue(stack, componentId, expectedValue);
    }

    public static String getComponentValue(ItemStack stack, String componentId) {
        return DATA.getComponentValue(stack, componentId);
    }

    public static boolean hasCustomDataKey(ItemStack stack, String key) {
        return DATA.hasCustomDataKey(stack, key);
    }

    public static ItemStack getMainHandItem(Player player) {
        if (player == null) return ItemStack.EMPTY;
        try {
            return player.getMainHandItem();
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }

    public static String getComponentsAsString(ItemStack stack) {
        return DATA.getComponentsAsString(stack);
    }

    public static String getCustomDataAsString(ItemStack stack) {
        return DATA.getCustomDataAsString(stack);
    }

    public static void clearCache() {
        DATA.clearCache();
    }
}
