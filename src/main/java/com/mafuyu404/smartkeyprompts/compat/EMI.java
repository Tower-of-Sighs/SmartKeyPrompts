package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
public class EMI {
    private static final String modid = "emi_skp";

    @SubscribeEvent
    public static void itemTooltip(ItemTooltipEvent event) {
        if (ModList.get().isLoaded("emi")) {
            SmartKeyPrompts.custom(modid,"key.keyboard.r", "key.emi.view_recipes");
            SmartKeyPrompts.custom(modid,"key.keyboard.u", "key.emi.view_uses");
            SmartKeyPrompts.custom(modid,"key.keyboard.a", "key.emi.favorite");
        }
    }
}
