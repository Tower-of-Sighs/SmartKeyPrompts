package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.util.CommonUtils;
import com.mafuyu404.smartkeyprompts.util.PromptUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = SmartKeyPrompts.MODID)
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