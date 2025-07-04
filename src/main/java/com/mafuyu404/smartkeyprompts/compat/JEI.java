package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.env.JeiCompat;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;

public class JEI {
    private static final String modid = "jei_skp";

    public static void init() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (FabricLoader.getInstance().isModLoaded("jei")) {
                SmartKeyPrompts.show(modid, "key.jei.showRecipe");
                SmartKeyPrompts.show(modid, "key.jei.showUses");
                SmartKeyPrompts.show(modid, "key.jei.bookmark");
                SmartKeyPrompts.show(modid, "key.jei.focusSearch");
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!FabricLoader.getInstance().isModLoaded("jei")) return;
            if (JeiCompat.isEnabled()) {
                SmartKeyPrompts.show(modid, "key.jei.recipeBack");
                SmartKeyPrompts.show(modid, "key.jei.toggleOverlay");
            }
        });
    }
}