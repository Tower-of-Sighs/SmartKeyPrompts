package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.init.Utils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.player.Player;

public class IceAndFire {
    private static final String modid = "iceandfire_skp";

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!FabricLoader.getInstance().isModLoaded("iceandfire")) return;
            Player player = client.player;
            if (player == null || client.screen != null) return;
            String vehicle = Utils.getVehicleType(player);
            if (vehicle != null && vehicle.startsWith("iceandfire:") && vehicle.endsWith("_dragon")) {
                SmartKeyPrompts.show(modid, "key.dragon_strike");
                SmartKeyPrompts.show(modid, "key.dragon_fireAttack");
                SmartKeyPrompts.show(modid, "key.dragon_down");
                SmartKeyPrompts.show(modid, "key.dragon_change_view");
            }
        });
    }
}