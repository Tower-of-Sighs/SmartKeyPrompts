package com.mafuyu404.smartkeyprompts.data;

import com.mafuyu404.oelib.event.FunctionRegistryEvent;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
public class SmartKeyPromptsFunctionRegistration {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onFunctionRegistration(FunctionRegistryEvent event) {
        event.registerFunctionClassSmart(DataPackFunctions.class, SmartKeyPrompts.MODID);

        SmartKeyPrompts.LOGGER.info("Registered SmartKeyPrompts functions to OELib (smart: {})",
                event.isSmartRegistration());
    }
}