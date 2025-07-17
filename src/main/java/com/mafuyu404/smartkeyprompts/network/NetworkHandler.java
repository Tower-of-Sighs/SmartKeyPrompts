package com.mafuyu404.smartkeyprompts.network;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SmartKeyPrompts.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;

        INSTANCE.registerMessage(id++, KeyPromptSyncChunkPacket.class,
                KeyPromptSyncChunkPacket::encode,
                KeyPromptSyncChunkPacket::decode,
                KeyPromptSyncChunkPacket::handle);

        SmartKeyPrompts.LOGGER.info("Registered network packets for chunked data transmission");
    }
}