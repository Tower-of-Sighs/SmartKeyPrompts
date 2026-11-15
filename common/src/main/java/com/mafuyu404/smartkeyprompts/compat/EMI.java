package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.platform.ClientPlatform;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.util.CommonUtils;
import com.mafuyu404.smartkeyprompts.util.PromptUtils;

public class EMI {
    private static final String modid = "emi_skp";

    public static void itemTooltip() {
        if (ClientPlatform.get().isModLoaded("emi")) {
            if (CommonUtils.isScreenOpen()) {
                PromptUtils.custom(modid, "key.keyboard.r", "key.emi.view_recipes");
                PromptUtils.custom(modid, "key.keyboard.u", "key.emi.view_uses");
                PromptUtils.custom(modid, "key.keyboard.a", "key.emi.favorite");
            }
        }
    }
}
