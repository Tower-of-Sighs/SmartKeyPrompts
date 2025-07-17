package com.mafuyu404.smartkeyprompts.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.data.KeyPromptData;
import com.mafuyu404.smartkeyprompts.data.KeyPromptDatapack;
import com.mafuyu404.smartkeyprompts.init.ReloadListener;
import net.minecraft.resources.ResourceLocation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 分片组装器，负责接收和组装分片数据
 */
public class ChunkAssembler {
    private static final Gson GSON = new Gson();
    private static final Type DATA_TYPE = new TypeToken<Map<ResourceLocation, KeyPromptData>>(){}.getType();
    
    // 存储正在组装的会话
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
    
    /**
     * 接收分片数据
     */
    public static void receiveChunk(UUID sessionId, int chunkIndex, int totalChunks, byte[] chunkData) {
        SmartKeyPrompts.LOGGER.debug("Received chunk {}/{} for session {}", chunkIndex + 1, totalChunks, sessionId);
        
        AssemblySession session = assemblingSessions.computeIfAbsent(sessionId, 
            id -> new AssemblySession(totalChunks));
        
        if (session.addChunk(chunkIndex, chunkData)) {
            // 所有分片都已接收，开始组装
            try {
                byte[] completeData = session.assembleData();
                String jsonData = new String(completeData);
                
                Map<ResourceLocation, KeyPromptData> data = GSON.fromJson(jsonData, DATA_TYPE);
                
                // 更新客户端数据
                KeyPromptDatapack.updateClientData(data);
                ReloadListener.onDataSyncReceived();
                
                SmartKeyPrompts.LOGGER.info("Successfully assembled and processed {} key prompt data files from {} chunks", 
                    data.size(), totalChunks);
                
            } catch (Exception e) {
                SmartKeyPrompts.LOGGER.error("Failed to assemble chunk data for session {}: {}", sessionId, e.getMessage(), e);
            } finally {
                // 清理已完成的会话
                assemblingSessions.remove(sessionId);
            }
        }
    }
    
    /**
     * 清理过期的会话
     */
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
    
    /**
     * 组装会话类
     */
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
        
        /**
         * 添加分片数据
         * @return 如果所有分片都已接收则返回true
         */
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
        
        /**
         * 组装完整数据
         */
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