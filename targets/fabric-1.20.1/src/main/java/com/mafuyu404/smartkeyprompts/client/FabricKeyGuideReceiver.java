package com.mafuyu404.smartkeyprompts.client;

import com.mafuyu404.smartkeyprompts.network.KeyGuideRequest;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/** Decodes bounded requests before forwarding them to the render thread. */
public final class FabricKeyGuideReceiver implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(KeyGuideRequest.CHANNEL, (client, handler, buffer, sender) -> {
            var request = KeyGuideRequest.read(buffer);
            client.execute(() -> KeyGuideClient.open(request.keys()));
        });
    }
}
