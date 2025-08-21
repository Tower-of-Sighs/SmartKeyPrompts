package com.mafuyu404.smartkeyprompts.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NBTUtils {

    private static final Map<String, NbtPathArgument.NbtPath> pathCache = new ConcurrentHashMap<>();
    private static final Map<String, CompoundTag> snbtCache = new ConcurrentHashMap<>();

    private static final int MAX_CACHE_SIZE = 200;

    /**
     * 获取缓存的NBT路径，如果不存在则解析并缓存
     */
    private static NbtPathArgument.NbtPath getCachedPath(String nbtPath) {
        return pathCache.computeIfAbsent(nbtPath, path -> {
            try {
                // 清理缓存以防止内存泄漏
                if (pathCache.size() > MAX_CACHE_SIZE) {
                    pathCache.clear();
                }
                return NbtPathArgument.nbtPath().parse(new StringReader(path));
            } catch (CommandSyntaxException e) {
                return null;
            }
        });
    }

    /**
     * 获取缓存的SNBT，如果不存在则解析并缓存
     */
    private static CompoundTag getCachedSNBT(String snbt) {
        return snbtCache.computeIfAbsent(snbt, s -> {
            try {
                if (snbtCache.size() > MAX_CACHE_SIZE) {
                    snbtCache.clear();
                }
                return TagParser.parseTag(s);
            } catch (CommandSyntaxException e) {
                return null;
            }
        });
    }

    /**
     * 检查NBT是否包含指定路径
     */
    public static boolean checkNBTPath(CompoundTag nbt, String nbtPath) {
        if (nbt == null || nbtPath == null || nbtPath.trim().isEmpty()) return false;

        try {
            NbtPathArgument.NbtPath path = getCachedPath(nbtPath);
            if (path == null) return false;

            return !path.get(nbt).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查NBT路径的值是否匹配期望值
     */
    public static boolean checkNBTValue(CompoundTag nbt, String nbtPath, String expectedValue) {
        if (nbt == null || nbtPath == null || expectedValue == null) return false;
        if (nbtPath.trim().isEmpty()) return false;

        try {
            NbtPathArgument.NbtPath path = getCachedPath(nbtPath);
            if (path == null) return false;

            var results = path.get(nbt);
            for (Tag tag : results) {
                if (expectedValue.equals(tag.getAsString())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取NBT路径的值
     */
    public static String getNBTValue(CompoundTag nbt, String nbtPath) {
        if (nbt == null || nbtPath == null || nbtPath.trim().isEmpty()) return null;

        try {
            NbtPathArgument.NbtPath path = getCachedPath(nbtPath);
            if (path == null) return null;

            var results = path.get(nbt);
            if (!results.isEmpty()) {
                return results.get(0).getAsString();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查NBT是否匹配SNBT格式
     */
    public static boolean matchSNBT(CompoundTag nbt, String snbt) {
        if (nbt == null || snbt == null || snbt.trim().isEmpty()) return false;

        try {
            CompoundTag expectedNbt = getCachedSNBT(snbt);
            if (expectedNbt == null) return false;

            return nbt.equals(expectedNbt);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取目标实体的NBT
     */
    public static CompoundTag getTargetEntityNBT() {
        try {
            Entity entity = PlayerUtils.getTargetedEntity();
            if (entity == null) return null;

            CompoundTag nbt = new CompoundTag();
            entity.saveWithoutId(nbt);
            return nbt;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取目标方块实体的NBT
     */
    public static CompoundTag getTargetBlockEntityNBT() {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (!(mc.hitResult instanceof BlockHitResult blockHit)) return null;

            BlockPos pos = blockHit.getBlockPos();
            var blockEntity = mc.level.getBlockEntity(pos);
            if (blockEntity == null) return null;

            return blockEntity.saveWithoutMetadata(mc.level.registryAccess());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取NBT的完整SNBT字符串表示
     */
    public static String getNBTAsString(CompoundTag nbt) {
        try {
            return nbt != null ? nbt.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 清理缓存（可选，用于内存管理）
     */
    public static void clearCache() {
        pathCache.clear();
        snbtCache.clear();
    }
}