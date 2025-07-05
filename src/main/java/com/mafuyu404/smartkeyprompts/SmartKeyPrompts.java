package com.mafuyu404.smartkeyprompts;

import com.mafuyu404.smartkeyprompts.init.HUD;
import com.mafuyu404.smartkeyprompts.init.Utils;
import com.mafuyu404.smartkeyprompts.utils.StrUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;


@Mod(SmartKeyPrompts.MOD_ID)
public class SmartKeyPrompts {

    public static final String MOD_ID = "smartkeyprompts";

    public static final Logger LOGGER = LogUtils.getLogger();

    public SmartKeyPrompts(IEventBus modEventBus, ModContainer container, Dist dist) {
        if (dist == Dist.CLIENT) {
            container.registerConfig(ModConfig.Type.COMMON, Config.CONFIG_SPEC, StrUtil.format("{}_config.toml", MOD_ID));
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
    }

    public static ResourceLocation ResourceLocationMod(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
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
