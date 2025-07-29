package com.mafuyu404.smartkeyprompts.data;

import com.mafuyu404.oelib.event.EventPriority;
import com.mafuyu404.oelib.event.Events;
import com.mafuyu404.oelib.event.FunctionRegistryEvent;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;


public class SmartKeyPromptsFunctionRegistration {
    public static void initialize() {
        Events.on(FunctionRegistryEvent.EVENT)
                .highest()
                .register(SmartKeyPromptsFunctionRegistration::onFunctionRegistration);
    }

    @EventPriority(priority = EventPriority.HIGHEST)
    public static void onFunctionRegistration(FunctionRegistryEvent event) {
        event.registerFunctionClassSmart(DataPackFunctions.class, SmartKeyPrompts.MODID);
    }
}