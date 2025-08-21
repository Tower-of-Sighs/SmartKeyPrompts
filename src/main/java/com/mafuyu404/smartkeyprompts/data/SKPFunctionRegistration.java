package com.mafuyu404.smartkeyprompts.data;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.api.FunctionRegistryEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = SmartKeyPrompts.MOD_ID, value = Dist.CLIENT)
public class SKPFunctionRegistration {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onFunctionRegistration(FunctionRegistryEvent event) {
        event.registerFunctionClass(DataPackFunctions.class, SmartKeyPrompts.MOD_ID);
    }
}