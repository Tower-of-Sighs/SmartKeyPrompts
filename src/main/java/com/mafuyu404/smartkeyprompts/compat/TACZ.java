package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
public class TACZ {
    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (!player.isLocalPlayer() || Minecraft.getInstance().screen != null) return;
        String[] type = player.getMainHandItem().getItem().getDescriptionId().split("\\.");
        if ((type[1] + ":" + type[2]).equals("tacz:modern_kinetic_gun")) {
            SmartKeyPrompts.show("tacz", "key.tacz.shoot.desc");
            SmartKeyPrompts.show("tacz", "key.tacz.zoom.desc");
            SmartKeyPrompts.show("tacz", "key.tacz.reload.desc");
            SmartKeyPrompts.show("tacz", "key.tacz.refit.desc");
            SmartKeyPrompts.show("tacz", "key.tacz.melee.desc");
            SmartKeyPrompts.show("tacz", "key.tacz.interact.desc");
            SmartKeyPrompts.show("tacz", "key.tacz.inspect.desc");
            SmartKeyPrompts.show("tacz", "key.tacz.fire_select.desc");
            SmartKeyPrompts.show("tacz", "key.tacz.crawl.desc");
            SmartKeyPrompts.show("tacz", "key.tacz.aim.desc");
        }
    }
}
