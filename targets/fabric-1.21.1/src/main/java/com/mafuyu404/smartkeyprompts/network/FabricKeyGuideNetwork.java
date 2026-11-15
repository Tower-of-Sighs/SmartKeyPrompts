package com.mafuyu404.smartkeyprompts.network;

import com.mafuyu404.smartkeyprompts.command.KeyGuideCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/** Registers the Minecraft 1.21.1 guide codec and command on both physical sides. */
public final class FabricKeyGuideNetwork implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(KeyGuidePayload.TYPE, KeyGuidePayload.CODEC);
        CommandRegistrationCallback.EVENT.register((dispatcher, registries, environment) ->
                KeyGuideCommand.register(dispatcher, (player, request) -> {
                    if (!ServerPlayNetworking.canSend(player, KeyGuidePayload.TYPE)) return false;
                    ServerPlayNetworking.send(player, new KeyGuidePayload(request));
                    return true;
                }));
    }
}
