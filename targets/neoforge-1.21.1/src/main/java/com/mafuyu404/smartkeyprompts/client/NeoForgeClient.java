package com.mafuyu404.smartkeyprompts.client;

import cc.sighs.oelib.config.ui.screen.ConfigScreen;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public final class NeoForgeClient {
    public static void initialize(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (minecraft, parent) -> new ConfigScreen(parent, SmartKeyPrompts.MODID));
    }
}
