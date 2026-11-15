package com.mafuyu404.smartkeyprompts;

import com.mafuyu404.smartkeyprompts.client.NeoForgeClient;
import com.mafuyu404.smartkeyprompts.network.NeoForgeKeyGuideNetwork;
import com.mafuyu404.smartkeyprompts.platform.OELibConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(SmartKeyPrompts.MODID)
public final class NeoForgeSmartKeyPrompts {
    public NeoForgeSmartKeyPrompts(IEventBus modEventBus, ModContainer container, Dist dist) {
        NeoForgeKeyGuideNetwork.register(modEventBus);
        OELibConfig.register();
        SmartKeyPrompts.initialize();
        if (dist == Dist.CLIENT) {
            NeoForgeClient.initialize(container);
        }
    }
}
