package com.mafuyu404.smartkeyprompts.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * Carries the ordered key binding identifiers shown by a developer's guide.
 *
 * <p>The request contains no key assignments; only the receiving player can
 * change local bindings. Identifiers are translation keys, not resource IDs.
 *
 * @param keys the one to sixteen key binding identifiers
 */
public record KeyGuideRequest(List<String> keys) {
    public static final int MAX_KEYS = 16;
    public static final int MAX_KEY_LENGTH = 256;
    public static final ResourceLocation CHANNEL = Objects.requireNonNull(
            ResourceLocation.tryParse("smartkeyprompts:key_guide"));

    /**
     * Copies and deduplicates valid identifiers while preserving their order.
     *
     * @param keys the identifiers to display
     * @throws IllegalArgumentException if the list or an identifier is invalid
     */
    public KeyGuideRequest {
        if (keys.isEmpty() || keys.size() > MAX_KEYS) {
            throw new IllegalArgumentException("Expected 1 to " + MAX_KEYS + " key bindings");
        }
        for (String key : keys) {
            if (key == null || key.isBlank() || key.length() > MAX_KEY_LENGTH
                    || key.chars().anyMatch(c -> Character.isWhitespace(c) || Character.isISOControl(c) || c == ',')) {
                throw new IllegalArgumentException("Invalid key binding identifier");
            }
        }
        keys = List.copyOf(new LinkedHashSet<>(keys));
    }

    /**
     * Parses space- or comma-separated identifiers used in the command.
     *
     * @param input the command's trailing argument
     * @return the validated request
     * @throws IllegalArgumentException if no valid bounded list was provided
     */
    public static KeyGuideRequest parse(String input) {
        return new KeyGuideRequest(List.of(input.strip().split("[\\s,]+")));
    }

    /**
     * Decodes a bounded request before any client state is accessed.
     *
     * @param buffer the incoming packet buffer
     * @return the validated request
     * @throws IllegalArgumentException if the encoded count or identifiers are invalid
     */
    public static KeyGuideRequest read(FriendlyByteBuf buffer) {
        int count = buffer.readVarInt();
        if (count < 1 || count > MAX_KEYS) {
            throw new IllegalArgumentException("Invalid key guide length: " + count);
        }
        List<String> keys = new ArrayList<>(count);
        for (int i = 0; i < count; i++) keys.add(buffer.readUtf(MAX_KEY_LENGTH));
        return new KeyGuideRequest(keys);
    }

    /**
     * Encodes the count and identifiers using bounded UTF strings.
     *
     * @param buffer the outgoing packet buffer
     */
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(keys.size());
        for (String key : keys) buffer.writeUtf(key, MAX_KEY_LENGTH);
    }
}
