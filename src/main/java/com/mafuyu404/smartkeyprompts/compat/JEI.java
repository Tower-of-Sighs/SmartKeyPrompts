package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.env.JeiCompat;
import com.mafuyu404.smartkeyprompts.util.CommonUtils;
import com.mafuyu404.smartkeyprompts.util.PromptUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;

public class JEI {
    private static final String modid = "jei_skp";

    public static void init() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (FabricLoader.getInstance().isModLoaded("jei")) {
                if (CommonUtils.isScreenOpen()) {
                    PromptUtils.show(modid, "key.jei.showRecipe");
                    PromptUtils.show(modid, "key.jei.showUses");
                    PromptUtils.show(modid, "key.jei.bookmark");
                    PromptUtils.show(modid, "key.jei.focusSearch");
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!FabricLoader.getInstance().isModLoaded("jei")) return;
            if (CommonUtils.isScreenOpen()) {
                if (JeiCompat.isEnabled()) {
                    PromptUtils.show(modid, "key.jei.recipeBack");
                    PromptUtils.show(modid, "key.jei.toggleOverlay");
                }
            }
        });
    }
}