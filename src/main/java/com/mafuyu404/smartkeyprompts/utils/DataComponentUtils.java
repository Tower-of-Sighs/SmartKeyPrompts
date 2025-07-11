package com.mafuyu404.smartkeyprompts.utils;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataComponentUtils {

    private static final Map<String, DataComponentType<?>> componentTypeCache = new ConcurrentHashMap<>();
    private static final int MAX_CACHE_SIZE = 200;

    /**
     * 获取缓存的数据组件类型
     */
    @SuppressWarnings("unchecked")
    private static <T> DataComponentType<T> getCachedComponentType(String componentId) {
        return (DataComponentType<T>) componentTypeCache.computeIfAbsent(componentId, id -> {
            try {
                // 清理缓存以防止内存泄漏
                if (componentTypeCache.size() > MAX_CACHE_SIZE) {
                    componentTypeCache.clear();
                }

                ResourceLocation resourceLocation = ResourceLocation.tryParse(id);
                if (resourceLocation == null) {
                    // 如果没有命名空间，使用默认
                    resourceLocation = ResourceLocation.withDefaultNamespace(id);
                }

                return BuiltInRegistries.DATA_COMPONENT_TYPE.get(resourceLocation);
            } catch (Exception e) {
                return null;
            }
        });
    }

    /**
     * 检查ItemStack是否包含指定的数据组件
     */
    public static boolean hasComponent(ItemStack itemStack, String componentId) {
        if (itemStack == null || itemStack.isEmpty() || componentId == null || componentId.trim().isEmpty()) {
            return false;
        }

        try {
            DataComponentType<?> componentType = getCachedComponentType(componentId);
            if (componentType == null) return false;

            return itemStack.has(componentType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查ItemStack数据组件的值是否匹配期望值
     */
    public static boolean checkComponentValue(ItemStack itemStack, String componentId, String expectedValue) {
        if (itemStack == null || itemStack.isEmpty() || componentId == null || expectedValue == null) {
            return false;
        }
        if (componentId.trim().isEmpty()) return false;

        try {
            DataComponentType<?> componentType = getCachedComponentType(componentId);
            if (componentType == null) return false;

            Object value = itemStack.get(componentType);
            if (value == null) return false;

            return expectedValue.equals(value.toString());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取ItemStack数据组件的值
     */
    public static String getComponentValue(ItemStack itemStack, String componentId) {
        if (itemStack == null || itemStack.isEmpty() || componentId == null || componentId.trim().isEmpty()) {
            return null;
        }

        try {
            DataComponentType<?> componentType = getCachedComponentType(componentId);
            if (componentType == null) return null;

            Object value = itemStack.get(componentType);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查ItemStack的自定义数据组件是否包含指定键
     */
    public static boolean hasCustomDataKey(ItemStack itemStack, String key) {
        if (itemStack == null || itemStack.isEmpty() || key == null || key.trim().isEmpty()) {
            return false;
        }

        try {
            CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            return customData.contains(key);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取玩家主手物品
     */
    public static ItemStack getMainHandItem(Player player) {
        if (player == null) return ItemStack.EMPTY;
        try {
            return player.getMainHandItem();
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }

    /**
     * 获取ItemStack的完整数据组件信息字符串
     */
    public static String getComponentsAsString(ItemStack itemStack) {
        try {
            if (itemStack == null || itemStack.isEmpty()) return null;

            // 获取所有数据组件的字符串表示
            return itemStack.getComponents().toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取ItemStack的自定义数据字符串表示
     */
    public static String getCustomDataAsString(ItemStack itemStack) {
        try {
            if (itemStack == null || itemStack.isEmpty()) return null;

            CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            return customData.isEmpty() ? null : customData.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 清理缓存
     */
    public static void clearCache() {
        componentTypeCache.clear();
    }
}