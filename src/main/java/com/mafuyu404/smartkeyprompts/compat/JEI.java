package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.env.JeiCompat;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = SmartKeyPrompts.MOD_ID)
public class JEI {
    private static final String modid = "jei_skp";

    @SubscribeEvent
    public static void itemTooltip(ItemTooltipEvent event) {
        if (ModList.get().isLoaded("jei")) {
            SmartKeyPrompts.show(modid, "key.jei.showRecipe");
            SmartKeyPrompts.show(modid, "key.jei.showUses");
            SmartKeyPrompts.show(modid, "key.jei.bookmark");
            SmartKeyPrompts.show(modid, "key.jei.focusSearch");
        }
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Pre event) {
        if (!ModList.get().isLoaded("jei")) return;
        if (JeiCompat.isEnabled()) {
            SmartKeyPrompts.show(modid, "key.jei.recipeBack");
            SmartKeyPrompts.show(modid, "key.jei.toggleOverlay");
        }
    }
}
