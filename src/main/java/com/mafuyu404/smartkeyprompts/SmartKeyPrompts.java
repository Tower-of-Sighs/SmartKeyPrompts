package com.mafuyu404.smartkeyprompts;

import com.mafuyu404.smartkeyprompts.init.HUD;
import com.mafuyu404.smartkeyprompts.init.Utils;
import net.fabricmc.api.ModInitializer;

public class SmartKeyPrompts implements ModInitializer {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "smartkeyprompts";

    @Override
    public void onInitialize() {
        ModConfig.register();
    }


    public static void show(String id, String desc) {
        Utils.getAllKeyBindings().forEach(keyBindingInfo -> {
            if (keyBindingInfo.desc().equals(desc)) {
                HUD.addCache(new HUD.KeyBindingInfo(id, keyBindingInfo.key(), keyBindingInfo.desc(), false));
            }
        });
    }

    public static void custom(String id, String key, String desc) {
        HUD.addCache(new HUD.KeyBindingInfo(id, key, desc, true));
    }

    public static void alias(String id, String key, String desc) {
        HUD.addCache(new HUD.KeyBindingInfo(id, key, desc, false));
    }


}
