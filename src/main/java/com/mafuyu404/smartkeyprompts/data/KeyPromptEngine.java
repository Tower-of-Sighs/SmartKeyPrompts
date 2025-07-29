package com.mafuyu404.smartkeyprompts.data;

import com.mafuyu404.oelib.core.DataManager;
import com.mafuyu404.oelib.core.DataRegistry;
import com.mafuyu404.oelib.core.ExpressionEngine;
import com.mafuyu404.oelib.event.DataReloadEvent;
import com.mafuyu404.oelib.event.EventPriority;
import com.mafuyu404.oelib.event.Events;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Environment(EnvType.CLIENT)
public class KeyPromptEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger("SmartKeyPrompts");
    private static final KeyPromptDataExtractor dataExtractor = new KeyPromptDataExtractor();
    private static DataManager<KeyPromptData> dataManager;
    private static boolean initialized = false;
    private static boolean dataLoaded = false;

    public static void initialize() {
        if (!initialized) {
            dataManager = DataManager.register(KeyPromptData.class);

            DataRegistry.register(KeyPromptData.class);
            DataRegistry.registerExtractor(KeyPromptData.class, dataExtractor);

            Events.on(DataReloadEvent.EVENT)
                    .normal()
                    .register(KeyPromptEngine::handleDataReload);

            ClientTickEvents.END_CLIENT_TICK.register(KeyPromptEngine::onClientTick);

            initialized = true;
            LOGGER.info("KeyPromptEngine initialized with OELib (Fabric)");
        }
    }

    private static void handleDataReload(Class<?> dataClass, int loadedCount, int invalidCount) {
        if (dataClass == KeyPromptData.class) {

            dataLoaded = true;
            checkAndInitializeExpressionEngine();
        }
    }

    private static void checkAndInitializeExpressionEngine() {
        if (dataLoaded && !DataRegistry.isExpressionEngineInitialized()) {
            LOGGER.info("Triggering smart function registration...");
            DataRegistry.initializeExpressionEngine();
        }
    }

    public static void onClientTick(Minecraft client) {
        if (!initialized) {
            initialize();
        }

        if (!DataRegistry.isExpressionEngineInitialized()) {
            return;
        }

        Player player = client.player;
        if (player == null || client.screen != null || dataManager == null) return;

        DataPackFunctions.setCurrentPlayer(player);

        Map<ResourceLocation, KeyPromptData> loadedData = dataManager.getAllData();
        for (Map.Entry<ResourceLocation, KeyPromptData> entry : loadedData.entrySet()) {
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


    public static DataManager<KeyPromptData> getDataManager() {
        if (!initialized) {
            initialize();
        }
        return dataManager;
    }


    public static void forceReload() {
        if (dataManager != null) {
            dataLoaded = false;
            DataRegistry.resetExpressionEngine();

            Minecraft.getInstance().reloadResourcePacks();
        }
    }
}