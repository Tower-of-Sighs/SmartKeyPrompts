package com.mafuyu404.smartkeyprompts.util;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;

public class PlayerUtils {
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
     * 获取玩家生命值百分比
     */
    public static float getHealthPercentage() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return 0.0f;
        return player.getHealth() / player.getMaxHealth();
    }

    /**
     * 获取玩家饥饿值
     */
    public static int getFoodLevel() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return 0;
        return player.getFoodData().getFoodLevel();
    }

    /**
     * 获取玩家经验等级
     */
    public static int getExperienceLevel() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return 0;
        return player.experienceLevel;
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
        return entity != null ? CommonUtils.toPathString(entity.getType().toString()) : null;
    }

    /**
     * 获取目标方块ID
     */
    public static String getTargetedBlockId() {
        BlockState blockState = getTargetedBlock();
        return blockState != null ? CommonUtils.toPathString(blockState.getBlock().getDescriptionId()) : null;
    }

    /**
     * 获取载具类型
     */
    public static String getVehicleType() {
        Player player = Minecraft.getInstance().player;
        if (player.getVehicle() == null) return null;
        return CommonUtils.toPathString(player.getVehicle().getType().toString());
    }

    /**
     * 获取主手物品ID
     */
    public static String getMainHandItemId() {
        try {
            ItemStack stack = Minecraft.getInstance().player.getMainHandItem();
            Item item = stack.getItem();
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);

            if (id == null) {
                SmartKeyPrompts.LOGGER.warn("[SKP] 主手物品注册名异常：{}", item.toString());
                return "unknown:unknown";
            }

            return id.toString();
        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error("[SKP] 获取主手物品ID失败", e);
            return "unknown:unknown";
        }
    }
}
