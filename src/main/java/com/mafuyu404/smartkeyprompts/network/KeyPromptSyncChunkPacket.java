package com.mafuyu404.smartkeyprompts.network;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * 分片数据包，用于传输大型数据包
 */
public class KeyPromptSyncChunkPacket {
    private final UUID sessionId;      // 会话ID，用于标识同一次传输
    private final int chunkIndex;      // 当前分片索引
    private final int totalChunks;     // 总分片数
    private final byte[] chunkData;    // 分片数据

    public KeyPromptSyncChunkPacket(UUID sessionId, int chunkIndex, int totalChunks, byte[] chunkData) {
        this.sessionId = sessionId;
        this.chunkIndex = chunkIndex;
        this.totalChunks = totalChunks;
        this.chunkData = chunkData;
    }

    public static void encode(KeyPromptSyncChunkPacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.sessionId);
        buf.writeInt(packet.chunkIndex);
        buf.writeInt(packet.totalChunks);
        buf.writeInt(packet.chunkData.length);
        buf.writeBytes(packet.chunkData);
    }

    public static KeyPromptSyncChunkPacket decode(FriendlyByteBuf buf) {
        UUID sessionId = buf.readUUID();
        int chunkIndex = buf.readInt();
        int totalChunks = buf.readInt();
        int dataLength = buf.readInt();
        byte[] chunkData = new byte[dataLength];
        buf.readBytes(chunkData);
        
        return new KeyPromptSyncChunkPacket(sessionId, chunkIndex, totalChunks, chunkData);
    }

    public static void handle(KeyPromptSyncChunkPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                ChunkAssembler.receiveChunk(packet.sessionId, packet.chunkIndex, packet.totalChunks, packet.chunkData);
            } catch (Exception e) {
                SmartKeyPrompts.LOGGER.error("Failed to handle chunk packet: {}", e.getMessage(), e);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public UUID getSessionId() { return sessionId; }
    public int getChunkIndex() { return chunkIndex; }
    public int getTotalChunks() { return totalChunks; }
    public byte[] getChunkData() { return chunkData; }
}