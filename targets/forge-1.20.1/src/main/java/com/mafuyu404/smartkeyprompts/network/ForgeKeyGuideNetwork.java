package com.mafuyu404.smartkeyprompts.network;

import com.mafuyu404.smartkeyprompts.client.KeyGuideClient;
import com.mafuyu404.smartkeyprompts.command.KeyGuideCommand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/** Keeps guide delivery optional for servers and clients without this feature. */
public final class ForgeKeyGuideNetwork {
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(KeyGuideRequest.CHANNEL,
            () -> "1", NetworkRegistry.acceptMissingOr("1"), NetworkRegistry.acceptMissingOr("1"));

    private ForgeKeyGuideNetwork() {
    }

    /** Registers the clientbound message and server command exactly once at mod startup. */
    public static void register() {
        CHANNEL.messageBuilder(KeyGuideRequest.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(KeyGuideRequest::write)
                .decoder(KeyGuideRequest::read)
                .consumerMainThread((request, context) -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> () -> KeyGuideClient.open(request.keys())))
                .add();
        MinecraftForge.EVENT_BUS.addListener(ForgeKeyGuideNetwork::registerCommands);
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        KeyGuideCommand.register(event.getDispatcher(), (player, request) -> {
            if (!CHANNEL.isRemotePresent(player.connection.connection)) return false;
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), request);
            return true;
        });
    }
}
