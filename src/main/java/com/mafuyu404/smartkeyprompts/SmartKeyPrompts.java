package com.mafuyu404.smartkeyprompts;

import com.mafuyu404.smartkeyprompts.event.ClientEvent;
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

    public static void show(String[] showList) {
        ArrayList<ClientEvent.KeyBindingInfo> bindingList = ClientEvent.getAllKeyBindings();
        ArrayList<ClientEvent.KeyBindingInfo> result = new ArrayList<>();
        bindingList.forEach(keyBindingInfo -> {
            if (List.of(showList).contains(keyBindingInfo.name())) result.add(keyBindingInfo);
        });
        ClientEvent.bindingInfoList = result;
    }
    public static void custom(String[] keyList, String[] descList) {
        ArrayList<ClientEvent.KeyBindingInfo> result = new ArrayList<>();
        for (int i = 0; i < keyList.length; i++) {
            result.add(new ClientEvent.KeyBindingInfo("", descList[i], Component.translatable(keyList[i]).getString()));
        }
        ClientEvent.bindingInfoList = result;
    }
}
