package com.mafuyu404.smartkeyprompts;

import com.mafuyu404.smartkeyprompts.init.HUD;
import com.mafuyu404.smartkeyprompts.init.KeyPrompt;
import com.mafuyu404.smartkeyprompts.util.KeyUtils;
import com.mafuyu404.smartkeyprompts.util.PromptUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PromptBehaviorTest {
    @AfterEach
    void clearState() {
        HUD.KeyPromptCache.clear();
        PromptUtils.enablePromptByGroup("test_group");
        PromptUtils.enablePromptByDesc("key.test");
        PromptUtils.enablePrompt("test_group", "key.test");
    }

    @Test
    void duplicatePromptsAreQueuedOnceAndRetainPresentation() {
        KeyPrompt first = new KeyPrompt("", "key.keyboard.k", "key.test", true)
                .withKeyAlias("K").atPosition("crosshair");
        first.toGroup("test_group");
        new KeyPrompt("", "key.keyboard.k", "key.test", true).toGroup("test_group");
        assertEquals(1, HUD.KeyPromptCache.size());
        assertEquals("crosshair", first.position);
        assertEquals("K", first.keyAlias);
        assertEquals("test_group:key.test/key.keyboard.k", first.getString());
    }

    @Test
    void changingKeyInvalidatesTheCachedIdentity() {
        KeyPrompt prompt = new KeyPrompt("test_group", "key.keyboard.k", "key.test", false);
        String oldIdentity = prompt.getString();
        prompt.forKey("key.keyboard.l");
        assertNotEquals(oldIdentity, prompt.getString());
        assertEquals(new KeyPrompt("test_group", "key.keyboard.l", "key.test", true), prompt);
    }

    @Test
    void groupDescriptionAndExactFiltersRemainIndependent() {
        KeyPrompt prompt = new KeyPrompt("test_group", "key.keyboard.k", "key.test", false);
        KeyPrompt other = new KeyPrompt("other_group", "key.keyboard.k", "key.test", false);
        PromptUtils.disablePrompt("test_group", "key.test");
        assertFalse(PromptUtils.checkPromptValid(prompt));
        assertTrue(PromptUtils.checkPromptValid(other));
        PromptUtils.enablePrompt("test_group", "key.test");
        assertTrue(PromptUtils.checkPromptValid(prompt));
        PromptUtils.disablePromptByDesc("key.test");
        assertFalse(PromptUtils.checkPromptValid(other));
        PromptUtils.enablePromptByDesc("key.test");
        PromptUtils.disablePromptByGroup("test_group");
        assertFalse(PromptUtils.checkPromptValid(prompt));
        assertTrue(PromptUtils.checkPromptValid(other));
    }

    @Test
    void unboundAndOutOfRangeKeyboardCodesNeverReachGlfw() {
        assertFalse(KeyUtils.isKeyPressed(-1));
        assertFalse(KeyUtils.isKeyPressed(0));
        assertFalse(KeyUtils.isKeyPressed(31));
        assertFalse(KeyUtils.isKeyPressed(349));
    }
}
