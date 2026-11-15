package com.mafuyu404.smartkeyprompts.client;

import com.mafuyu404.smartkeyprompts.compat.JEI;
import com.mafuyu404.smartkeyprompts.compat.EMI;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

public final class FabricClientInitializer extends FabricClientEvents {
    @Override
    protected void registerTooltip() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> { JEI.itemTooltip(); EMI.itemTooltip(); });
    }
    @Override
    protected void tickBlade() {
        com.mafuyu404.smartkeyprompts.compat.SlashBladeIntegration.tick();
    }
}
