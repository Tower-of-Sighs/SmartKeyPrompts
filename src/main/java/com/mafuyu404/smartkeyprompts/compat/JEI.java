package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.env.JeiCompat;
import com.mafuyu404.smartkeyprompts.util.CommonUtils;
import com.mafuyu404.smartkeyprompts.util.PromptUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
public class JEI {
    private static final String modid = "jei_skp";

    @SubscribeEvent
    public static void itemTooltip(ItemTooltipEvent event) {
        if (ModList.get().isLoaded("jei")) {
            if (CommonUtils.isScreenOpen()) {
                PromptUtils.show(modid, "key.jei.showRecipe");
                PromptUtils.show(modid, "key.jei.showUses");
                PromptUtils.show(modid, "key.jei.bookmark");
            }
        }
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Pre event) {
        if (CommonUtils.isScreenOpen()) {
            // SmartKeyPrompts.addDesc("key.jei.showRecipe").atPosition("crosshair").toGroup("jei_skp");
            // SmartKeyPrompts.addDesc("key.jei.bookmark").withCustom(true).forKey("key.mouse.4").atPosition("crosshair").toGroup("jei_skp");
            if (!ModList.get().isLoaded("jei"))
                return;
            if (JeiCompat.isEnabled()) {
                PromptUtils.show(modid, "key.jei.recipeBack");
                PromptUtils.show(modid, "key.jei.toggleOverlay");
            }
        }
    }
}
