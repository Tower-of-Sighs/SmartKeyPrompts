package com.mafuyu404.smartkeyprompts.network;

import com.mafuyu404.smartkeyprompts.client.KeyGuideClient;
import com.mafuyu404.smartkeyprompts.command.KeyGuideCommand;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Registers an optional server-to-client tutorial channel and its command. */
public final class NeoForgeKeyGuideNetwork {
    private NeoForgeKeyGuideNetwork() {
    }

    /**
     * Attaches packet registration and server command listeners.
     *
     * @param modBus the mod lifecycle event bus
     */
    public static void register(IEventBus modBus) {
        modBus.addListener(NeoForgeKeyGuideNetwork::registerPayloads);
        NeoForge.EVENT_BUS.addListener(NeoForgeKeyGuideNetwork::registerCommands);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").optional().playToClient(KeyGuidePayload.TYPE, KeyGuidePayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> KeyGuideClient.open(payload.request().keys())));
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        KeyGuideCommand.register(event.getDispatcher(), (player, request) -> {
            if (!player.connection.hasChannel(KeyGuidePayload.TYPE)) return false;
            PacketDistributor.sendToPlayer(player, new KeyGuidePayload(request));
            return true;
        });
    }
}
