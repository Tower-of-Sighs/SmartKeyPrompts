package com.mafuyu404.smartkeyprompts.network;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.data.KeyPromptData;
import com.mafuyu404.smartkeyprompts.util.GsonUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class KeyPromptSyncPacket {
    private static final int MAX_CHUNK_SIZE = 30000; // 30KB

    private final Map<ResourceLocation, KeyPromptData> data;

    public KeyPromptSyncPacket(Map<ResourceLocation, KeyPromptData> data) {
        this.data = data;
    }

    public void sendTo(ServerPlayer player) {
        sendToTarget(PacketDistributor.PLAYER.with(() -> player));
    }

    public void sendToAll() {
        sendToTarget(PacketDistributor.ALL.noArg());
    }

    private void sendToTarget(PacketDistributor.PacketTarget target) {
        try {
            String jsonData = GsonUtils.getGson().toJson(data);
            byte[] dataBytes = jsonData.getBytes(StandardCharsets.UTF_8);

            SmartKeyPrompts.LOGGER.info("Sending key prompt data: {} files, {} bytes", data.size(), dataBytes.length);

            if (dataBytes.length <= MAX_CHUNK_SIZE) {
                sendSingleChunk(target, dataBytes);
            } else {
                sendChunked(target, dataBytes);
            }

        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error("Failed to send key prompt sync packet: {}", e.getMessage(), e);
        }
    }

    private void sendSingleChunk(PacketDistributor.PacketTarget target, byte[] dataBytes) {
        UUID sessionId = UUID.randomUUID();
        KeyPromptSyncChunkPacket chunk = new KeyPromptSyncChunkPacket(sessionId, 0, 1, dataBytes);
        NetworkHandler.INSTANCE.send(target, chunk);
        SmartKeyPrompts.LOGGER.debug("Sent single chunk for session {}", sessionId);
    }

    private void sendChunked(PacketDistributor.PacketTarget target, byte[] data) {
        UUID sessionId = UUID.randomUUID();
        int totalChunks = (int) Math.ceil((double) data.length / MAX_CHUNK_SIZE);

        SmartKeyPrompts.LOGGER.info("Splitting data into {} chunks for session {}", totalChunks, sessionId);

        for (int i = 0; i < totalChunks; i++) {
            int start = i * MAX_CHUNK_SIZE;
            int end = Math.min(start + MAX_CHUNK_SIZE, data.length);
            int chunkSize = end - start;

            byte[] chunkData = new byte[chunkSize];
            System.arraycopy(data, start, chunkData, 0, chunkSize);

            KeyPromptSyncChunkPacket chunk = new KeyPromptSyncChunkPacket(sessionId, i, totalChunks, chunkData);
            NetworkHandler.INSTANCE.send(target, chunk);

            SmartKeyPrompts.LOGGER.debug("Sent chunk {}/{} ({} bytes) for session {}",
                    i + 1, totalChunks, chunkSize, sessionId);
        }
    }
}