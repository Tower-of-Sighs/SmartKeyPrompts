package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.util.PlayerUtils;
import com.mafuyu404.smartkeyprompts.util.PromptUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class IceAndFire {
    private static final String modid = "iceandfire_skp";

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(IceAndFire::tick);
    }

    public static void tick(Minecraft client) {
        if (!FabricLoader.getInstance().isModLoaded("iceandfire")) return;
        Player player = Minecraft.getInstance().player;
        if (player == null || Minecraft.getInstance().screen != null) return;
        String vehicle = PlayerUtils.getVehicleType();
        if (vehicle != null && vehicle.startsWith("iceandfire:") && vehicle.endsWith("_dragon")) {
            PromptUtils.show(modid, "key.dragon_strike");
            PromptUtils.show(modid, "key.dragon_fireAttack");
            PromptUtils.show(modid, "key.dragon_down");
            PromptUtils.show(modid, "key.dragon_change_view");
        }
    }
}
