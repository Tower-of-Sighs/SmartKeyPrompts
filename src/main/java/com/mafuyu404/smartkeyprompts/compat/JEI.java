package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.env.JeiCompat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
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
    public static void tick(TickEvent.ClientTickEvent event) {
        if (!ModList.get().isLoaded("jei")) return;
        if (JeiCompat.isEnabled()) {
            SmartKeyPrompts.show(modid, "key.jei.recipeBack");
            SmartKeyPrompts.show(modid, "key.jei.toggleOverlay");
        }
    }
}
