package com.mafuyu404.smartkeyprompts.env;

import com.mafuyu404.smartkeyprompts.platform.ClientPlatform;

public class JeiCompat {
    private static final String MOD_ID = "jei";
    private static boolean INSTALLED = false;

    public static void init() {
        INSTALLED = ClientPlatform.get().isModLoaded(MOD_ID);
    }

    public static boolean isEnabled() {
        if (INSTALLED) {
            return JeiPlugin.isEnabled();
        }
        return false;
    }
}

