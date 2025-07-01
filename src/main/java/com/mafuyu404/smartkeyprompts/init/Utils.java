package com.mafuyu404.smartkeyprompts.init;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Utils {
    public static String getVehicleType(Player player) {
        if (player.getVehicle() == null) return null;
        return toPathString(player.getVehicle().getType().toString());
    }
    public static String getMainHandItemId(Player player) {
        return toPathString(player.getMainHandItem().getItem().getDescriptionId());
    }
    public static String toPathString(String key) {
        String[] path = key.split("\\.");
        return path[1] + ":" + path[2];
    }

    public static String getKeyByDesc(String desc) {
        for (HUD.KeyBindingInfo keyBindingInfo : HUD.getAllKeyBindings()) {
            if (keyBindingInfo.desc().equals(desc)) return keyBindingInfo.key();
        }
        return null;
    }

    public static Entity getTargetedEntity(Player player, double extendReachDistance) {
        AttributeInstance reachAttr = player.getAttribute(ForgeMod.BLOCK_REACH.get());
        double maxDistance = reachAttr != null ? reachAttr.getValue() : 4.5; // 默认值
        maxDistance += extendReachDistance;

        Vec3 eyePosition = player.getEyePosition();
        Vec3 viewVector = player.getLookAngle();
        Vec3 endPosition = eyePosition.add(viewVector.x * maxDistance, viewVector.y * maxDistance, viewVector.z * maxDistance);

        EntityHitResult result = player.level().clip(new ClipContext(
                eyePosition,
                endPosition,
                ClipContext.Block.OUTLINE, // 方块检测模式
                ClipContext.Fluid.NONE, // 忽略流体
                player
        )).getType() == HitResult.Type.BLOCK
                ? null // 如果视线被方块阻挡则跳过实体检测
                : ProjectileUtil.getEntityHitResult(
                player.level(),
                player,
                eyePosition,
                endPosition,
                new AABB(eyePosition, endPosition).inflate(1.0D), // 检测范围
                entity -> !entity.isSpectator() && entity.isPickable() && entity != player
        );

        return result != null ? result.getEntity() : null;
    }
}
