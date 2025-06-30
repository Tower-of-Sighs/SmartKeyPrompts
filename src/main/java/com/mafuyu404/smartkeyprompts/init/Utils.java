package com.mafuyu404.smartkeyprompts.init;

import net.minecraft.world.entity.player.Player;

import java.util.Objects;

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
}
