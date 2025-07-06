package com.mafuyu404.smartkeyprompts.data;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.api.FunctionRegistryEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
public class SmartKeyPromptsFunctionRegistration {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onFunctionRegistration(FunctionRegistryEvent event) {
        event.registerFunctionClass(DataPackFunctions.class, SmartKeyPrompts.MODID);
    }
}