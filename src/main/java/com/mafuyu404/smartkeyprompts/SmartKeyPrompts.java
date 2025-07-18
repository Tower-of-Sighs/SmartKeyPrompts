package com.mafuyu404.smartkeyprompts;

import com.mafuyu404.smartkeyprompts.init.HUD;
import com.mafuyu404.smartkeyprompts.init.KeyPrompt;
import com.mafuyu404.smartkeyprompts.init.Utils;
import com.mafuyu404.smartkeyprompts.network.NetworkHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SmartKeyPrompts.MODID)
public class SmartKeyPrompts {

    public static final String MODID = "smartkeyprompts";

    public static final Logger LOGGER = LogManager.getLogger(SmartKeyPrompts.MODID);

    public SmartKeyPrompts() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkHandler::register);
    }

    public static void show(String id, String desc) {
        Utils.getAllKeyBindings().forEach(keyPrompt -> {
            if (keyPrompt.desc.equals(desc)) {
                HUD.addCache(new KeyPrompt(id, keyPrompt.key, keyPrompt.desc, false));
            }
        });
    }

    public static void custom(String id, String key, String desc) {
        HUD.addCache(new KeyPrompt(id, key, desc, true));
    }

    public static void alias(String id, String key, String desc) {
        HUD.addCache(new KeyPrompt(id, key, desc, false));
    }

    public static KeyPrompt addDesc(String desc) {
        return new KeyPrompt("", Utils.getKeyByDesc(desc), desc, false);
    }
}