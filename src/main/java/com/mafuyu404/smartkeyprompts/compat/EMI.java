package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.util.CommonUtils;
import com.mafuyu404.smartkeyprompts.util.PromptUtils;
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
            if (CommonUtils.isScreenOpen()) {
                PromptUtils.custom(modid, "key.keyboard.r", "key.emi.view_recipes");
                PromptUtils.custom(modid, "key.keyboard.u", "key.emi.view_uses");
                PromptUtils.custom(modid, "key.keyboard.a", "key.emi.favorite");
            }
        }
    }
}