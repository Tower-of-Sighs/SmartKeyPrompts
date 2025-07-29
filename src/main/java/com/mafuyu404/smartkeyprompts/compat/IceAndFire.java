package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.util.PlayerUtils;
import com.mafuyu404.smartkeyprompts.util.PromptUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
public class IceAndFire {
    private static final String modid = "iceandfire_skp";

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (!ModList.get().isLoaded("iceandfire")) return;
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
