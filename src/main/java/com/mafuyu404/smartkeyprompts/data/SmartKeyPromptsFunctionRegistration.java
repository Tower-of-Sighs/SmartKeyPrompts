package com.mafuyu404.smartkeyprompts.data;

import com.mafuyu404.oelib.neoforge.event.FunctionRegistryEvent;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
public class SmartKeyPromptsFunctionRegistration {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onFunctionRegistration(FunctionRegistryEvent event) {
        event.registerFunctionClassSmart(DataPackFunctions.class, SmartKeyPrompts.MODID);

        SmartKeyPrompts.LOGGER.info("Registered SmartKeyPrompts functions to OELib (smart: {})",
                event.isSmartRegistration());
    }
}