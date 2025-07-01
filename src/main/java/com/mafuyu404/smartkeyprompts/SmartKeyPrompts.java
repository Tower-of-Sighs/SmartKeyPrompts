package com.mafuyu404.smartkeyprompts;

import com.mafuyu404.smartkeyprompts.init.HUD;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SmartKeyPrompts.MODID)
public class SmartKeyPrompts {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "smartkeyprompts";

    public SmartKeyPrompts() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }


    public static void show(String id, String desc) {
        HUD.getAllKeyBindings().forEach(keyBindingInfo -> {
            if (keyBindingInfo.desc().equals(desc)) {
                HUD.addCache(new HUD.KeyBindingInfo(id, keyBindingInfo.key(), keyBindingInfo.desc(), false));
            }
        });
    }
    public static void custom(String id, String key, String desc) {
        HUD.addCache(new HUD.KeyBindingInfo(id, key, desc, true));
    }
}
