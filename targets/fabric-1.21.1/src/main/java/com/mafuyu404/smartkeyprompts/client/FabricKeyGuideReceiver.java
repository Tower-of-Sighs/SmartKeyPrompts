package com.mafuyu404.smartkeyprompts.client;

import com.mafuyu404.smartkeyprompts.network.KeyGuidePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/** Delivers guide requests to the client's pending-screen queue. */
public final class FabricKeyGuideReceiver implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(KeyGuidePayload.TYPE,
                (payload, context) -> KeyGuideClient.open(payload.request().keys()));
    }
}
