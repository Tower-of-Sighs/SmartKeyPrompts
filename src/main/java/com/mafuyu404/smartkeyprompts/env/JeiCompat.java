package com.mafuyu404.smartkeyprompts.env;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

@Environment(EnvType.CLIENT)
public class JeiCompat {
    private static final String MOD_ID = "jei";
    private static boolean INSTALLED = false;

    public static void init() {
        INSTALLED = FabricLoader.getInstance().isModLoaded(MOD_ID);
    }

    public static boolean isEnabled() {
        if (INSTALLED) {
            return JeiPlugin.isEnabled();
        }
        return false;
    }
}