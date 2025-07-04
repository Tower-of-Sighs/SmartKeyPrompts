package com.mafuyu404.smartkeyprompts.init;

import com.mafuyu404.smartkeyprompts.compat.DiligentStalker;
import com.mafuyu404.smartkeyprompts.compat.IceAndFire;
import com.mafuyu404.smartkeyprompts.compat.ImmersiveAircraft;
import com.mafuyu404.smartkeyprompts.compat.JEI;
import com.mafuyu404.smartkeyprompts.env.JeiCompat;
import net.fabricmc.api.ClientModInitializer;

public class ClientSetupEvent implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModKeybindings.register();
        HUD.init();
        ConfigAction.init();
        JeiCompat.init();
        JEI.init();
        DiligentStalker.init();
        IceAndFire.init();
        ImmersiveAircraft.init();
    }
}
