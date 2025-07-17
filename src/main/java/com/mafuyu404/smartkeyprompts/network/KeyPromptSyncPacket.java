package com.mafuyu404.smartkeyprompts.network;

import com.google.gson.Gson;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.data.KeyPromptData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 主同步包，负责将数据分片并发送
 */
public class KeyPromptSyncPacket {
    private static final Gson GSON = new Gson();
    private static final int MAX_CHUNK_SIZE = 30000; // 每个分片最大30KB，确保不超过网络包限制

    private final Map<ResourceLocation, KeyPromptData> data;

    public KeyPromptSyncPacket(Map<ResourceLocation, KeyPromptData> data) {
        this.data = data;
    }

    /**
     * 发送数据到指定玩家
     */
    public void sendTo(ServerPlayer player) {
        sendToTarget(PacketDistributor.PLAYER.with(() -> player));
    }

    /**
     * 发送数据到所有玩家
     */
    public void sendToAll() {
        sendToTarget(PacketDistributor.ALL.noArg());
    }

    /**
     * 发送数据到指定目标
     */
    private void sendToTarget(PacketDistributor.PacketTarget target) {
        try {
            // 将数据序列化为JSON
            String jsonData = GSON.toJson(data);
            byte[] dataBytes = jsonData.getBytes(StandardCharsets.UTF_8);

            SmartKeyPrompts.LOGGER.info("Sending key prompt data: {} files, {} bytes", data.size(), dataBytes.length);

            if (dataBytes.length <= MAX_CHUNK_SIZE) {
                // 数据较小，直接发送单个分片
                UUID sessionId = UUID.randomUUID();
                KeyPromptSyncChunkPacket chunk = new KeyPromptSyncChunkPacket(sessionId, 0, 1, dataBytes);
                NetworkHandler.INSTANCE.send(target, chunk);
                SmartKeyPrompts.LOGGER.debug("Sent single chunk for session {}", sessionId);
            } else {
                // 数据较大，分片发送
                sendChunked(target, dataBytes);
            }

        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error("Failed to send key prompt sync packet: {}", e.getMessage(), e);
        }
    }

    /**
     * 分片发送数据
     */
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