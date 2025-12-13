package com.mafuyu404.smartkeyprompts.compat.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
            return screen -> SKPClothConfigScreen.getConfigBuilder().setParentScreen(screen).build();
        }
        return screen -> null;
    }
}