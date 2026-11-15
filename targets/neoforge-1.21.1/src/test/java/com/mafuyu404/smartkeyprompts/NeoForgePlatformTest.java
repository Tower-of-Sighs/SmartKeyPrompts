package com.mafuyu404.smartkeyprompts;

import com.mafuyu404.smartkeyprompts.platform.ClientPlatform;
import com.mafuyu404.smartkeyprompts.platform.NeoForgeClientPlatform;
import org.junit.jupiter.api.Test;

import java.util.ServiceLoader;

import static org.junit.jupiter.api.Assertions.*;

class NeoForgePlatformTest {
    @Test
    void targetPackagesExactlyOneClientProvider() {
        var providers = ServiceLoader.load(ClientPlatform.class).stream().toList();
        assertEquals(1, providers.size());
        assertEquals(NeoForgeClientPlatform.class, providers.getFirst().type());
        assertInstanceOf(NeoForgeClientPlatform.class, ClientPlatform.get());
    }
}
