package com.mafuyu404.smartkeyprompts;

import cc.sighs.oelib.config.ui.screen.ConfigScreen;
import com.mafuyu404.smartkeyprompts.network.ForgeKeyGuideNetwork;
import com.mafuyu404.smartkeyprompts.platform.OELibConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

/** Initializes shared data and Forge's client configuration entry. */
@Mod(SmartKeyPrompts.MODID)
public final class ForgeSmartKeyPrompts {
    public ForgeSmartKeyPrompts() {
        ForgeKeyGuideNetwork.register();
        OELibConfig.register();
        SmartKeyPrompts.initialize();
        if (FMLLoader.getDist() == Dist.CLIENT) {
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, parent) ->
                            new ConfigScreen(parent, SmartKeyPrompts.MODID)));
        }
    }
}
