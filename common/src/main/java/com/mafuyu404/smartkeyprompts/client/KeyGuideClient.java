package com.mafuyu404.smartkeyprompts.client;

import com.mafuyu404.smartkeyprompts.init.HUD;
import com.mafuyu404.smartkeyprompts.init.KeyPrompt;
import com.mafuyu404.smartkeyprompts.platform.ClientPlatform;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.LinkedHashSet;
import java.util.List;

/** Maintains developer-requested HUD guides and their optional binding editor. */
public final class KeyGuideClient {
    /** The maximum number of actions shown by one guide. */
    public static final int MAX_BINDINGS = 16;
    /** The prompt group used while a developer-requested guide is active. */
    public static final String PROMPT_GROUP = "smartkeyprompts_guide";
    private static List<String> bindingIds = List.of();

    private KeyGuideClient() {
    }

    /**
     * Displays a HUD guide for the given registered key binding names.
     *
     * <p>Blank names and duplicates are removed, with the first sixteen distinct
     * names retained. A newer request replaces the active HUD guide.
     * Unknown names remain visible as unavailable actions. This method schedules
     * its state change on the client thread and does not change any bindings.
     *
     * @param bindingIds the list of key binding translation identifiers
     */
    public static void open(List<String> bindingIds) {
        List<String> names = normalize(bindingIds);
        Minecraft.getInstance().execute(() -> KeyGuideClient.bindingIds = names);
    }

    /** Refreshes the active guide's HUD prompts without opening a screen. */
    public static void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        KeyMapping control = ClientPlatform.get().controlKey();
        if (bindingIds.isEmpty() || minecraft.player == null) return;
        for (String id : bindingIds) {
            KeyMapping mapping = findBinding(id);
            String key = mapping == null ? InputConstants.UNKNOWN.getName() : ClientPlatform.get().key(mapping).getName();
            String label = mapping == null ? Component.translatable("screen.smartkeyprompts.guide.unavailable").getString()
                    : mapping.getTranslatedKeyMessage().getString();
            new KeyPrompt(PROMPT_GROUP, key, id, mapping == null, label).toGroup(PROMPT_GROUP);
        }
        String shortcut = control.getTranslatedKeyMessage().getString() + " + "
                + Component.translatable("key.mouse.left").getString();
        new KeyPrompt(PROMPT_GROUP, ClientPlatform.get().key(control).getName() + "+key.mouse.left",
                "key.smartkeyprompts.guide_edit", true, shortcut).toGroup(PROMPT_GROUP);
    }

    /**
     * Returns the active guide's identifiers in display order.
     *
     * @return the immutable list of action identifiers, empty when no guide is active
     */
    public static List<String> getBindingIds() {
        return bindingIds;
    }

    /**
     * Opens the active guide's editor when the player is in unobstructed gameplay.
     *
     * @return whether the editor was opened
     */
    public static boolean openEditor() {
        Minecraft minecraft = Minecraft.getInstance();
        if (bindingIds.isEmpty() || minecraft.player == null || minecraft.screen != null || HUD.KeyMappingCache != null) {
            return false;
        }
        minecraft.setScreen(new KeyGuideScreen(bindingIds));
        return true;
    }

    /** Removes the active HUD guide when dismissed or disconnected. */
    public static void clear() {
        bindingIds = List.of();
        HUD.KeyPromptCache.removeIf(prompt -> PROMPT_GROUP.equals(prompt.group));
        synchronized (HUD.KeyPromptList) {
            HUD.KeyPromptList.removeIf(prompt -> PROMPT_GROUP.equals(prompt.group));
        }
        HUD.clearCache();
    }

    static KeyMapping findBinding(String id) {
        for (KeyMapping mapping : Minecraft.getInstance().options.keyMappings) {
            if (id.equals(mapping.getName())) return mapping;
        }
        return null;
    }

    static List<String> normalize(List<String> bindingIds) {
        LinkedHashSet<String> names = new LinkedHashSet<>();
        for (String name : bindingIds) {
            if (name != null && !name.isBlank()) {
                names.add(name.trim());
                if (names.size() == MAX_BINDINGS) {
                    break;
                }
            }
        }
        return List.copyOf(names);
    }
}
