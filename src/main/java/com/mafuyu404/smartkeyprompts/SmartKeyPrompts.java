package com.mafuyu404.smartkeyprompts;

import com.mafuyu404.smartkeyprompts.event.HUD;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.ArrayList;
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

    public static void show(String id, String[] showList) {
        ArrayList<HUD.KeyBindingInfo> bindingList = HUD.getAllKeyBindings();
        ArrayList<HUD.KeyBindingInfo> result = new ArrayList<>();
        bindingList.forEach(keyBindingInfo -> {
            if (List.of(showList).contains(keyBindingInfo.name())) result.add(keyBindingInfo);
        });
        HUD.bindingInfoCache.put(id, result);
    }
    public static void custom(String id, String[] keyList, String[] descList) {
        ArrayList<HUD.KeyBindingInfo> result = new ArrayList<>();
        for (int i = 0; i < keyList.length; i++) {
            result.add(new HUD.KeyBindingInfo("", descList[i], Component.translatable(keyList[i]).getString()));
        }
        HUD.bindingInfoCache.put(id, result);
    }
}
