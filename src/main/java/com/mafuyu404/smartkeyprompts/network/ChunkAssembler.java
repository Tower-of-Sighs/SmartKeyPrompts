package com.mafuyu404.smartkeyprompts.network;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.data.KeyPromptData;
import com.mafuyu404.smartkeyprompts.data.KeyPromptDatapack;
import com.mafuyu404.smartkeyprompts.init.ReloadListener;
import com.mafuyu404.smartkeyprompts.util.GsonUtils;
import net.minecraft.resources.ResourceLocation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChunkAssembler {
    private static final Map<UUID, AssemblySession> assemblingSessions = new ConcurrentHashMap<>();

    // 清理过期会话的调度器
    private static final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "ChunkAssembler-Cleanup");
        t.setDaemon(true);
        return t;
    });

    static {
        // 每30秒清理一次过期会话
        cleanupExecutor.scheduleAtFixedRate(ChunkAssembler::cleanupExpiredSessions, 30, 30, TimeUnit.SECONDS);
    }

    public static void receiveChunk(UUID sessionId, int chunkIndex, int totalChunks, byte[] chunkData) {
        SmartKeyPrompts.LOGGER.debug("Received chunk {}/{} for session {}", chunkIndex + 1, totalChunks, sessionId);

        AssemblySession session = assemblingSessions.computeIfAbsent(sessionId,
                id -> new AssemblySession(totalChunks));

        if (session.addChunk(chunkIndex, chunkData)) {
            try {
                byte[] completeData = session.assembleData();
                String jsonData = new String(completeData);

                SmartKeyPrompts.LOGGER.debug("Attempting to parse JSON data for session {}: {}",
                        sessionId, jsonData.length() > 500 ? jsonData.substring(0, 500) + "..." : jsonData);

                Map<ResourceLocation, KeyPromptData> data = parseJsonData(jsonData, sessionId);

                KeyPromptDatapack.updateClientData(data);
                ReloadListener.onDataSyncReceived();

                SmartKeyPrompts.LOGGER.info("Successfully assembled and processed {} key prompt data files from {} chunks",
                        data.size(), totalChunks);

            } catch (Exception e) {
                SmartKeyPrompts.LOGGER.error("Failed to assemble chunk data for session {}: {}", sessionId, e.getMessage(), e);
                logFailedJsonContent(sessionId);
            } finally {
                assemblingSessions.remove(sessionId);
            }
        }
    }


    private static Map<ResourceLocation, KeyPromptData> parseJsonData(String jsonData, UUID sessionId) {
        try {
            return GsonUtils.getGson().fromJson(jsonData, GsonUtils.KEY_PROMPT_DATA_MAP_TYPE);
        } catch (com.google.gson.JsonSyntaxException e) {
            SmartKeyPrompts.LOGGER.error("JSON parsing failed for session {}. JSON content preview (first 1000 chars): {}",
                    sessionId, jsonData.length() > 1000 ? jsonData.substring(0, 1000) + "..." : jsonData);

            GsonUtils.analyzeJsonContent(jsonData, sessionId.toString());

            throw new RuntimeException("Failed to parse JSON data for session " + sessionId +
                    ". Error: " + e.getMessage() +
                    ". JSON preview: " + (jsonData.length() > 200 ? jsonData.substring(0, 200) + "..." : jsonData), e);
        }
    }

    private static void logFailedJsonContent(UUID sessionId) {
        try {
            AssemblySession session = assemblingSessions.get(sessionId);
            if (session != null) {
                byte[] completeData = session.assembleData();
                String jsonData = new String(completeData);
                SmartKeyPrompts.LOGGER.error("Failed JSON content for session {} (length: {}): {}",
                        sessionId, jsonData.length(),
                        jsonData.length() > 1000 ? jsonData.substring(0, 1000) + "..." : jsonData);
            }
        } catch (Exception debugException) {
            SmartKeyPrompts.LOGGER.error("Could not retrieve JSON data for debugging: {}", debugException.getMessage());
        }
    }

    private static void cleanupExpiredSessions() {
        long currentTime = System.currentTimeMillis();
        assemblingSessions.entrySet().removeIf(entry -> {
            boolean expired = currentTime - entry.getValue().getCreationTime() > 60000; // 60秒超时
            if (expired) {
                SmartKeyPrompts.LOGGER.warn("Cleaning up expired assembly session: {}", entry.getKey());
            }
            return expired;
        });
    }

    private static class AssemblySession {
        private final int totalChunks;
        private final byte[][] chunks;
        private final boolean[] received;
        private final long creationTime;
        private int receivedCount = 0;

        public AssemblySession(int totalChunks) {
            this.totalChunks = totalChunks;
            this.chunks = new byte[totalChunks][];
            this.received = new boolean[totalChunks];
            this.creationTime = System.currentTimeMillis();
        }

        public synchronized boolean addChunk(int chunkIndex, byte[] chunkData) {
            if (chunkIndex < 0 || chunkIndex >= totalChunks) {
                SmartKeyPrompts.LOGGER.warn("Invalid chunk index: {} (total: {})", chunkIndex, totalChunks);
                return false;
            }

            if (!received[chunkIndex]) {
                chunks[chunkIndex] = chunkData;
                received[chunkIndex] = true;
                receivedCount++;
            }

            return receivedCount == totalChunks;
        }

        public byte[] assembleData() throws IOException {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            for (int i = 0; i < totalChunks; i++) {
                if (chunks[i] == null) {
                    throw new IOException("Missing chunk: " + i);
                }
                output.write(chunks[i]);
            }
            return output.toByteArray();
        }

        public long getCreationTime() {
            return creationTime;
        }
    }
}