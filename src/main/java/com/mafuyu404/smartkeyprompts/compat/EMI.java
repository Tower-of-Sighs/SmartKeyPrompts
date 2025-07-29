package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.util.CommonUtils;
import com.mafuyu404.smartkeyprompts.util.PromptUtils;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;

public class EMI {
    private static final String modid = "emi_skp";

    public static void init() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (FabricLoader.getInstance().isModLoaded("emi")) {
                if (CommonUtils.isScreenOpen()) {
                    PromptUtils.custom(modid, "key.keyboard.r", "key.emi.view_recipes");
                    PromptUtils.custom(modid, "key.keyboard.u", "key.emi.view_uses");
                    PromptUtils.custom(modid, "key.keyboard.a", "key.emi.favorite");
                }
            }
        });
    }
}