package com.mafuyu404.smartkeyprompts.compat.config;

import cc.sighs.oelib.config.ui.screen.ConfigScreen;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ConfigScreen(parent, SmartKeyPrompts.MODID);
    }
}