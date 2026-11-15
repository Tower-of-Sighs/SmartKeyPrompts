package com.mafuyu404.smartkeyprompts.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class KeyGuideRequestTest {
    @Test
    void keepsDeveloperOrderAndDeduplicatesMixedSeparators() {
        var request = KeyGuideRequest.parse("key.jump, mod.dodge key.jump\tkey.sprint");
        assertEquals(List.of("key.jump", "mod.dodge", "key.sprint"), request.keys());
        assertThrows(UnsupportedOperationException.class, () -> request.keys().add("key.sneak"));
    }

    @Test
    void rejectsEmptyOverlongAndOversizedRequests() {
        assertThrows(IllegalArgumentException.class, () -> KeyGuideRequest.parse("  "));
        assertThrows(IllegalArgumentException.class, () -> KeyGuideRequest.parse("key." + "x".repeat(256)));
        assertThrows(IllegalArgumentException.class, () -> new KeyGuideRequest(List.of("key.bad\nkey.jump")));
        assertThrows(IllegalArgumentException.class, () -> new KeyGuideRequest(
                IntStream.range(0, 17).mapToObj(i -> "key." + i).toList()));
    }

    @Test
    void preservesIdentifiersAcrossNetworkRoundTrip() {
        var original = new KeyGuideRequest(List.of("key.jump", "mod.modded_action"));
        var buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            original.write(buffer);
            assertEquals(original, KeyGuideRequest.read(buffer));
            assertFalse(buffer.isReadable());
        } finally {
            buffer.release();
        }
    }

    @Test
    void rejectsPacketCountBeforeAllocatingOrReadingStrings() {
        for (int count : new int[]{-1, 0, 17, Integer.MAX_VALUE}) {
            var buffer = new FriendlyByteBuf(Unpooled.buffer());
            try {
                buffer.writeVarInt(count);
                assertThrows(IllegalArgumentException.class, () -> KeyGuideRequest.read(buffer));
            } finally {
                buffer.release();
            }
        }
    }
}
