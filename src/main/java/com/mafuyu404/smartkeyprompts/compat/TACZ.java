package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.init.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
public class TACZ {
    private static final String modid = "tacz_skp";

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (!ModList.get().isLoaded("tacz")) return;
        Player player = Minecraft.getInstance().player;
        if (player == null || Minecraft.getInstance().screen != null) return;
        if (Utils.getMainHandItemId(player).equals("tacz:modern_kinetic_gun")) {
            SmartKeyPrompts.show(modid, "key.tacz.shoot.desc");
            SmartKeyPrompts.show(modid, "key.tacz.zoom.desc");
            SmartKeyPrompts.show(modid, "key.tacz.reload.desc");
            SmartKeyPrompts.show(modid, "key.tacz.refit.desc");
            SmartKeyPrompts.show(modid, "key.tacz.melee.desc");
            SmartKeyPrompts.show(modid, "key.tacz.interact.desc");
            SmartKeyPrompts.show(modid, "key.tacz.inspect.desc");
            SmartKeyPrompts.show(modid, "key.tacz.fire_select.desc");
            SmartKeyPrompts.show(modid, "key.tacz.crawl.desc");
            SmartKeyPrompts.show(modid, "key.tacz.aim.desc");
        }
    }
}
