package com.mafuyu404.smartkeyprompts.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.network.KeyPromptSyncPacket;
import com.mafuyu404.smartkeyprompts.network.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KeyPromptDatapack extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<ResourceLocation, KeyPromptData> loadedData = new HashMap<>();
    private static KeyPromptDatapack instance;

    public KeyPromptDatapack() {
        super(GSON, SmartKeyPrompts.MODID);
        instance = this;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        loadedData.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
            ResourceLocation location = entry.getKey();
            JsonElement json = entry.getValue();

            try {
                KeyPromptData data = GSON.fromJson(json, KeyPromptData.class);
                loadedData.put(location, data);
                SmartKeyPrompts.LOGGER.info("Loaded key prompt data: {}", location);
            } catch (Exception e) {
                SmartKeyPrompts.LOGGER.error("Failed to parse key prompt data: {}", location, e);
            }
        }

        SmartKeyPrompts.LOGGER.info("Loaded {} key prompt data files", loadedData.size());

        // 同步到所有在线玩家
        syncToAllPlayers();
    }

    private void syncToAllPlayers() {
        if (!loadedData.isEmpty()) {
            KeyPromptSyncPacket packet = new KeyPromptSyncPacket(new HashMap<>(loadedData));
            packet.sendToAll();
        }
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new KeyPromptDatapack());
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !loadedData.isEmpty()) {
            // 向新加入的玩家同步数据
            KeyPromptSyncPacket packet = new KeyPromptSyncPacket(new HashMap<>(loadedData));
            packet.sendTo(player);
        }
    }

    public static Map<ResourceLocation, KeyPromptData> getLoadedData() {
        return new HashMap<>(loadedData);
    }

    public static void updateClientData(Map<ResourceLocation, KeyPromptData> data) {
        loadedData.clear();
        loadedData.putAll(data);
        SmartKeyPrompts.LOGGER.info("Updated client key prompt data: {} files", data.size());
    }
}