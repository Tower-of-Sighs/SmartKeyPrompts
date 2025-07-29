package com.mafuyu404.smartkeyprompts.init;

import com.mafuyu404.smartkeyprompts.compat.EMI;
import com.mafuyu404.smartkeyprompts.compat.IceAndFire;
import com.mafuyu404.smartkeyprompts.compat.JEI;
import com.mafuyu404.smartkeyprompts.data.KeyPromptEngine;
import com.mafuyu404.smartkeyprompts.data.SmartKeyPromptsFunctionRegistration;
import com.mafuyu404.smartkeyprompts.env.JeiCompat;
import net.fabricmc.api.ClientModInitializer;

public class ClientSetupEvent implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModKeybindings.register();
        KeyStateManager.init();
        HUD.init();
        ConfigAction.init();
        JeiCompat.init();
        JEI.init();
        EMI.init();
        IceAndFire.init();
        SmartKeyPromptsFunctionRegistration.initialize();
        KeyPromptEngine.initialize();
    }
}