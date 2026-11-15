package com.mafuyu404.smartkeyprompts.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Wraps a guide request in the Minecraft 1.21.1 custom payload protocol.
 *
 * @param request the bounded list of binding identifiers
 */
public record KeyGuidePayload(KeyGuideRequest request) implements CustomPacketPayload {
    public static final Type<KeyGuidePayload> TYPE = new Type<>(KeyGuideRequest.CHANNEL);
    public static final StreamCodec<RegistryFriendlyByteBuf, KeyGuidePayload> CODEC = StreamCodec.of(
            (buffer, payload) -> payload.request.write(buffer),
            buffer -> new KeyGuidePayload(KeyGuideRequest.read(buffer)));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
