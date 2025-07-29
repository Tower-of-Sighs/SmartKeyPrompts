package com.mafuyu404.smartkeyprompts;

import com.mafuyu404.smartkeyprompts.init.KeyPrompt;
import com.mafuyu404.smartkeyprompts.util.KeyUtils;
import com.mafuyu404.smartkeyprompts.util.PromptUtils;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartKeyPrompts implements ModInitializer {

    public static final String MODID = "smartkeyprompts";
    public static final Logger LOGGER = LoggerFactory.getLogger(SmartKeyPrompts.class);

    @Override
    public void onInitialize() {
        ModConfig.register();
    }


    @Deprecated
    public static void show(String id, String desc) {
        PromptUtils.addDesc(desc).toGroup(id);
    }

    @Deprecated
    public static void custom(String id, String key, String desc) {
        PromptUtils.addDesc(desc).forKey(key).withCustom(true).toGroup(id);
    }

    @Deprecated
    public static void alias(String id, String desc, String alias) {
        PromptUtils.addDesc(desc).withKeyAlias(alias).toGroup(id);
    }

    @Deprecated
    public static KeyPrompt addDesc(String desc) {
        return new KeyPrompt("", KeyUtils.getKeyByDesc(desc), desc, false);
    }
}
