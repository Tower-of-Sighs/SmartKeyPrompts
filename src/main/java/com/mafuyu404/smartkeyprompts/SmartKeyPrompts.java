package com.mafuyu404.smartkeyprompts;

import com.mafuyu404.oelib.data.DataRegistry;
import com.mafuyu404.smartkeyprompts.data.KeyPromptData;
import com.mafuyu404.smartkeyprompts.data.KeyPromptDataExtractor;
import com.mafuyu404.smartkeyprompts.init.KeyPrompt;
import com.mafuyu404.smartkeyprompts.util.KeyUtils;
import com.mafuyu404.smartkeyprompts.util.PromptUtils;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SmartKeyPrompts implements ModInitializer {

    public static final String MODID = "smartkeyprompts";

    public static final Logger LOGGER = LogManager.getLogger(SmartKeyPrompts.MODID);

    @Override
    public void onInitialize() {
        NeoForgeConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.CLIENT, Config.SPEC);
        DataRegistry.register(KeyPromptData.class, KeyPromptData.CODEC);
        DataRegistry.registerExtractor(KeyPromptData.class, new KeyPromptDataExtractor());
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
