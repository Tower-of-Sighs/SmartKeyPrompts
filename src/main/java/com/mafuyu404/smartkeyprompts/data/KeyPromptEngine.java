package com.mafuyu404.smartkeyprompts.data;

import com.mafuyu404.oelib.data.DataManagerBridge;
import com.mafuyu404.oelib.data.DataRegistry;
import com.mafuyu404.oelib.data.mvel.ExpressionEngine;
import com.mafuyu404.oelib.forge.event.DataReloadEvent;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
public class KeyPromptEngine {
    private static volatile Map<ResourceLocation, KeyPromptData> cachedData = Map.of();

    @SubscribeEvent
    public static void onDataReload(DataReloadEvent event) {
        if (event.getDataClass() == KeyPromptData.class) {
            SmartKeyPrompts.LOGGER.info("KeyPrompt data reloaded: {} entries loaded, {} invalid",
                    event.getLoadedCount(), event.getInvalidCount());
            cachedData = DataManagerBridge.getAllData(KeyPromptData.class);
            DataRegistry.resetExpressionEngine();
            DataRegistry.initializeExpressionEngine();
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (!DataRegistry.isExpressionEngineInitialized()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.screen != null) return;

        DataPackFunctions.setCurrentPlayer(player);

        for (Map.Entry<ResourceLocation, KeyPromptData> entry : cachedData.entrySet()) {
            KeyPromptData data = entry.getValue();
            if (!ExpressionEngine.checkModLoadedCondition(data.vars())) {
                continue;
            }
            processKeyPromptData(data, player);
        }
    }

    private static void processKeyPromptData(KeyPromptData data, Player player) {
        Map<String, Object> context = ExpressionEngine.createContext(data.vars());

        for (KeyPromptData.Entry entry : data.entries()) {
            if (ExpressionEngine.checkConditions(entry.when(), context)) {
                ExpressionEngine.executeActions(entry.then(), context);
            }
        }
    }
}