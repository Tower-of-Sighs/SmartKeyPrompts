package com.mafuyu404.smartkeyprompts.network;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.data.KeyPromptData;
import com.mafuyu404.smartkeyprompts.data.KeyPromptDatapack;
import com.mafuyu404.smartkeyprompts.data.KeyPromptEngine;
import com.mafuyu404.smartkeyprompts.init.HUD;
import com.mafuyu404.smartkeyprompts.util.CodecUtils;
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
    private static final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "ChunkAssembler-Cleanup");
        t.setDaemon(true);
        return t;
    });

    static {
        cleanupExecutor.scheduleAtFixedRate(ChunkAssembler::cleanupExpiredSessions, 30, 30, TimeUnit.SECONDS);
    }

    public static void receiveChunk(UUID sessionId, int chunkIndex, int totalChunks, byte[] chunkData) {
        AssemblySession session = assemblingSessions.computeIfAbsent(sessionId,
                id -> new AssemblySession(totalChunks));

        if (session.addChunk(chunkIndex, chunkData)) {
            try {
                byte[] completeData = session.assembleData();
                String jsonData = new String(completeData);
                Map<ResourceLocation, KeyPromptData> data = parseJsonData(jsonData, sessionId);

                if (data != null) {
                    KeyPromptDatapack.updateClientData(data);
                    KeyPromptEngine.forceReloadWithData(data);
                    HUD.clearCache();

                    SmartKeyPrompts.LOGGER.info("Successfully processed {} key prompt data files", data.size());
                } else {
                    SmartKeyPrompts.LOGGER.error("Failed to parse JSON data for session {}", sessionId);
                }

            } catch (Exception e) {
                SmartKeyPrompts.LOGGER.error("Failed to assemble chunk data for session {}: {}", sessionId, e.getMessage(), e);
            } finally {
                assemblingSessions.remove(sessionId);
            }
        }
    }

    private static Map<ResourceLocation, KeyPromptData> parseJsonData(String jsonData, UUID sessionId) {
        try {
            Map<ResourceLocation, KeyPromptData> result = CodecUtils.decodeFromJson(jsonData);
            if (result == null) {
                SmartKeyPrompts.LOGGER.error("Codec parsing returned null for session {}", sessionId);
                CodecUtils.analyzeJsonContent(jsonData, sessionId.toString());
            }
            return result;
        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error("Codec parsing failed for session {}: {}", sessionId, e.getMessage(), e);
            CodecUtils.analyzeJsonContent(jsonData, sessionId.toString());
            throw new RuntimeException("Failed to parse JSON data for session " + sessionId, e);
        }
    }

    private static void cleanupExpiredSessions() {
        long currentTime = System.currentTimeMillis();
        assemblingSessions.entrySet().removeIf(entry -> {
            boolean expired = currentTime - entry.getValue().getCreationTime() > 60000;
            if (expired) {
                SmartKeyPrompts.LOGGER.debug("Cleaning up expired assembly session: {}", entry.getKey());
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