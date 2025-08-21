package com.mafuyu404.smartkeyprompts.data;

import com.mafuyu404.oelib.neoforge.data.DataManager;
import com.mafuyu404.oelib.neoforge.data.DataRegistry;
import com.mafuyu404.oelib.neoforge.data.mvel.ExpressionEngine;
import com.mafuyu404.oelib.neoforge.event.DataReloadEvent;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.Map;

@EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
public class KeyPromptEngine {
    private static final KeyPromptDataExtractor dataExtractor = new KeyPromptDataExtractor();
    private static DataManager<KeyPromptData> dataManager;
    private static boolean initialized = false;
    private static boolean dataLoaded = false;

    public static void initialize() {
        if (!initialized) {
            dataManager = DataManager.register(KeyPromptData.class);

            DataRegistry.register(KeyPromptData.class);
            DataRegistry.registerExtractor(KeyPromptData.class, dataExtractor);

            initialized = true;
            SmartKeyPrompts.LOGGER.info("KeyPromptEngine initialized with OELib");
        }
    }

    @SubscribeEvent
    public static void onDataReload(DataReloadEvent event) {
        if (event.getDataClass() == KeyPromptData.class) {
            SmartKeyPrompts.LOGGER.info("KeyPrompt data reloaded: {} entries loaded, {} invalid",
                    event.getLoadedCount(), event.getInvalidCount());

            dataLoaded = true;

            checkAndInitializeExpressionEngine();
        }
    }

    private static void checkAndInitializeExpressionEngine() {
        if (dataLoaded && !DataRegistry.isExpressionEngineInitialized()) {
            SmartKeyPrompts.LOGGER.info("Triggering smart function registration...");
            DataRegistry.initializeExpressionEngine();
        }
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Pre event) {
        if (!initialized) {
            initialize();
        }


        if (!DataRegistry.isExpressionEngineInitialized()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.screen != null || dataManager == null) return;

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