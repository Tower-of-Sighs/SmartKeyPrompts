package com.mafuyu404.smartkeyprompts;

import com.mafuyu404.smartkeyprompts.platform.OELibConfig;
import net.fabricmc.api.ModInitializer;

public final class FabricSmartKeyPrompts implements ModInitializer {
    @Override
    public void onInitialize() {
        OELibConfig.register();
        SmartKeyPrompts.initialize();
    }
}
