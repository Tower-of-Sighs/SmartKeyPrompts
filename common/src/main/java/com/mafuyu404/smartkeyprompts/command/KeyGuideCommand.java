package com.mafuyu404.smartkeyprompts.command;

import com.mafuyu404.smartkeyprompts.network.KeyGuideRequest;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.function.BiPredicate;

/** Registers the operator command used by tutorials, functions and quest scripts. */
public final class KeyGuideCommand {
    private static final SimpleCommandExceptionType INVALID_KEYS = new SimpleCommandExceptionType(
            Component.translatable("command.smartkeyprompts.guide.invalid", KeyGuideRequest.MAX_KEYS));
    private static final SimpleCommandExceptionType NO_RECEIVERS = new SimpleCommandExceptionType(
            Component.translatable("command.smartkeyprompts.guide.no_receivers"));

    private KeyGuideCommand() {
    }

    /**
     * Registers {@code /skp guide <targets> <keys...>} with permission level two.
     *
     * @param dispatcher the server command dispatcher
     * @param sender the platform sender, returning false for an unsupported client
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                BiPredicate<ServerPlayer, KeyGuideRequest> sender) {
        var root = dispatcher.register(Commands.literal("smartkeyprompts")
                .then(Commands.literal("guide")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("keys", StringArgumentType.greedyString())
                                        .executes(context -> send(context.getSource(),
                                                EntityArgument.getPlayers(context, "targets"),
                                                StringArgumentType.getString(context, "keys"), sender))))));
        dispatcher.register(Commands.literal("skp").redirect(root));
    }

    private static int send(CommandSourceStack source, Collection<ServerPlayer> targets, String input,
                            BiPredicate<ServerPlayer, KeyGuideRequest> sender) throws CommandSyntaxException {
        KeyGuideRequest request;
        try {
            request = KeyGuideRequest.parse(input);
        } catch (IllegalArgumentException exception) {
            throw INVALID_KEYS.create();
        }
        int sent = 0;
        for (ServerPlayer player : targets) {
            if (sender.test(player, request)) sent++;
        }
        if (sent == 0) throw NO_RECEIVERS.create();
        int count = sent;
        source.sendSuccess(() -> Component.translatable("command.smartkeyprompts.guide.sent", count), false);
        int skipped = targets.size() - sent;
        if (skipped != 0) {
            source.sendFailure(Component.translatable("command.smartkeyprompts.guide.skipped", skipped));
        }
        return sent;
    }
}
