package com.mafuyu404.smartkeyprompts.client;

import com.mafuyu404.smartkeyprompts.compat.EMI;
import com.mafuyu404.smartkeyprompts.compat.IceAndFire;
import com.mafuyu404.smartkeyprompts.compat.JEI;
import com.mafuyu404.smartkeyprompts.compat.SlashBlade;
import com.mafuyu404.smartkeyprompts.data.KeyPromptEngine;
import com.mafuyu404.smartkeyprompts.env.JeiCompat;
import com.mafuyu404.smartkeyprompts.init.ConfigAction;
import com.mafuyu404.smartkeyprompts.init.HUD;
import com.mafuyu404.smartkeyprompts.init.KeyStateManager;
import com.mafuyu404.smartkeyprompts.init.ModKeybindings;
import net.fabricmc.api.ClientModInitializer;

public class SmartKeyPromptsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModKeybindings.registerKeyMapping();
        KeyPromptEngine.init();
        JeiCompat.init();
        ConfigAction.init();
        HUD.init();
        KeyStateManager.init();
        EMI.init();
        IceAndFire.init();
        JEI.init();
        SlashBlade.init();
    }
}
