package com.mafuyu404.smartkeyprompts.network;

import com.mafuyu404.smartkeyprompts.command.KeyGuideCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/** Registers guide delivery through Fabric's Minecraft 1.20.1 byte-buffer channel. */
public final class FabricKeyGuideNetwork implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registries, environment) ->
                KeyGuideCommand.register(dispatcher, (player, request) -> {
                    if (!ServerPlayNetworking.canSend(player, KeyGuideRequest.CHANNEL)) return false;
                    var buffer = PacketByteBufs.create();
                    request.write(buffer);
                    ServerPlayNetworking.send(player, KeyGuideRequest.CHANNEL, buffer);
                    return true;
                }));
    }
}
