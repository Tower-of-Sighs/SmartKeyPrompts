package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.init.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = SmartKeyPrompts.MOD_ID)
public class IceAndFire {
    private static final String modid = "iceandfire_skp";

    @SubscribeEvent
    public static void tick(ClientTickEvent.Pre event) {
        if (!ModList.get().isLoaded("iceandfire")) return;
        Player player = Minecraft.getInstance().player;
        if (player == null || Minecraft.getInstance().screen != null) return;
        String vehicle = Utils.getVehicleType(player);
        if (vehicle != null && vehicle.startsWith("iceandfire:") && vehicle.endsWith("_dragon")) {
            SmartKeyPrompts.show(modid, "key.dragon_strike");
            SmartKeyPrompts.show(modid, "key.dragon_fireAttack");
            SmartKeyPrompts.show(modid, "key.dragon_down");
            SmartKeyPrompts.show(modid, "key.dragon_change_view");
        }
    }
}
