package com.mafuyu404.smartkeyprompts.data;

import com.mafuyu404.oelib.data.DataManagerBridge;
import com.mafuyu404.oelib.data.DataRegistry;
import com.mafuyu404.oelib.data.mvel.ExpressionEngine;
import com.mafuyu404.oelib.fabric.event.DataReloadEvent;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class KeyPromptEngine {
    private static volatile Map<ResourceLocation, KeyPromptData> cachedData = Map.of();

    public static void init() {
        DataReloadEvent.EVENT.register(KeyPromptEngine::onDataReload);
        ClientTickEvents.START_CLIENT_TICK.register(KeyPromptEngine::tick);
    }

    public static void onDataReload(Class<?> dataClass, int loadedCount, int invalidCount) {
        if (dataClass == KeyPromptData.class) {
            SmartKeyPrompts.LOGGER.info("KeyPrompt data reloaded: {} entries loaded, {} invalid",
                    loadedCount, invalidCount);
            cachedData = DataManagerBridge.getAllData(KeyPromptData.class);
            DataRegistry.resetExpressionEngine();
            DataRegistry.initializeExpressionEngine();
        }
    }

    public static void tick(Minecraft client) {
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