package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.platform.ClientPlatform;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.env.JeiCompat;
import com.mafuyu404.smartkeyprompts.util.CommonUtils;
import com.mafuyu404.smartkeyprompts.util.PromptUtils;

public class JEI {
    private static final String modid = "jei_skp";

    public static void itemTooltip() {
        if (ClientPlatform.get().isModLoaded("jei")) {
            if (CommonUtils.isScreenOpen()) {
                PromptUtils.show(modid, "key.jei.showRecipe");
                PromptUtils.show(modid, "key.jei.showUses");
                PromptUtils.show(modid, "key.jei.bookmark");
            }
        }
    }

    public static void tick() {
        if (CommonUtils.isScreenOpen()) {
            // SmartKeyPrompts.addDesc("key.jei.showRecipe").atPosition("crosshair").toGroup("jei_skp");
            // SmartKeyPrompts.addDesc("key.jei.bookmark").withCustom(true).forKey("key.mouse.4").atPosition("crosshair").toGroup("jei_skp");
            if (!ClientPlatform.get().isModLoaded("jei"))
                return;
            if (JeiCompat.isEnabled()) {
                PromptUtils.show(modid, "key.jei.recipeBack");
                PromptUtils.show(modid, "key.jei.toggleOverlay");
            }
        }
    }
}

