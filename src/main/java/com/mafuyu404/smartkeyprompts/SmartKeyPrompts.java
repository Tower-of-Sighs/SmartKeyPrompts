package com.mafuyu404.smartkeyprompts;

import com.mafuyu404.smartkeyprompts.init.KeyPrompt;
import com.mafuyu404.smartkeyprompts.util.KeyUtils;
import com.mafuyu404.smartkeyprompts.util.PromptUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SmartKeyPrompts.MODID)
public class SmartKeyPrompts {

    public static final String MODID = "smartkeyprompts";

    public static final Logger LOGGER = LogManager.getLogger(SmartKeyPrompts.MODID);

    public SmartKeyPrompts(IEventBus modEventBus, ModContainer container, Dist dist) {
        if (dist == Dist.CLIENT) {
            container.registerConfig(ModConfig.Type.COMMON, Config.CONFIG_SPEC, MODID + "_config.toml");
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
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