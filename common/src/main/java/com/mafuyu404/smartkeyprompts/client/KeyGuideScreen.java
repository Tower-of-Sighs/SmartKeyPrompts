package com.mafuyu404.smartkeyprompts.client;

import com.mafuyu404.smartkeyprompts.init.HUD;
import com.mafuyu404.smartkeyprompts.platform.ClientPlatform;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows a focused set of actions and lets the player edit their own bindings.
 *
 * <p>Each completed edit is immediately saved to the client's normal options.
 * Escape cancels an in-progress capture; it never silently unbinds an action.
 */
public final class KeyGuideScreen extends Screen {
    private static final int ROW_HEIGHT = 24;
    private static final int ROW_TOP = 58;
    private final List<String> bindingIds;
    private final List<Row> rows = new ArrayList<>();
    private KeyMapping selected;
    private InputConstants.Key heldModifier = InputConstants.UNKNOWN;
    private int page;
    private int pageSize;
    private int panelX;
    private int panelWidth;

    /**
     * Creates a guide containing at most sixteen distinct action identifiers.
     *
     * @param bindingIds the list of key binding translation identifiers
     */
    public KeyGuideScreen(List<String> bindingIds) {
        super(Component.translatable("screen.smartkeyprompts.guide.title"));
        this.bindingIds = KeyGuideClient.normalize(bindingIds);
    }

    /**
     * Returns the action identifiers requested by this guide in display order.
     *
     * @return the immutable list of action identifiers
     */
    public List<String> getBindingIds() {
        return bindingIds;
    }

    /**
     * Returns the action currently waiting for keyboard or mouse input.
     *
     * @return the action identifier, or {@code null} when no capture is active
     */
    public String getSelectedBindingId() {
        return selected == null ? null : selected.getName();
    }

    @Override
    protected void init() {
        panelWidth = Math.min(360, width - 24);
        panelX = (width - panelWidth) / 2;
        pageSize = Math.max(1, (height - ROW_TOP - 68) / ROW_HEIGHT);
        page = Math.min(page, pageCount() - 1);
        rows.clear();
        int end = Math.min(bindingIds.size(), (page + 1) * pageSize);
        for (int index = page * pageSize; index < end; index++) {
            String id = bindingIds.get(index);
            KeyMapping mapping = KeyGuideClient.findBinding(id);
            int y = ROW_TOP + (index - page * pageSize) * ROW_HEIGHT;
            Button binding = addRenderableWidget(Button.builder(bindingLabel(mapping), button -> {
                selected = mapping;
                updateButtons();
            }).bounds(panelX + panelWidth - 182, y, 82, 20).build());
            Button reset = addRenderableWidget(Button.builder(text("reset"), button -> {
                ClientPlatform.get().resetKey(mapping);
                saveEdit();
            }).bounds(panelX + panelWidth - 96, y, 50, 20).build());
            Button clear = addRenderableWidget(Button.builder(text("clear"), button -> {
                ClientPlatform.get().setKey(mapping, InputConstants.UNKNOWN, InputConstants.UNKNOWN);
                saveEdit();
            }).bounds(panelX + panelWidth - 42, y, 42, 20).build());
            reset.setTooltip(Tooltip.create(text("reset_hint")));
            clear.setTooltip(Tooltip.create(text("clear_hint")));
            rows.add(new Row(id, mapping, binding, reset, clear, y));
        }
        Button previous = addRenderableWidget(Button.builder(Component.literal("<"), button -> changePage(-1))
                .bounds(panelX, height - 28, 28, 20).build());
        previous.setTooltip(Tooltip.create(text("previous")));
        previous.active = page > 0;
        Button next = addRenderableWidget(Button.builder(Component.literal(">"), button -> changePage(1))
                .bounds(panelX + panelWidth - 28, height - 28, 28, 20).build());
        next.setTooltip(Tooltip.create(text("next")));
        next.active = page + 1 < pageCount();
        addRenderableWidget(Button.builder(text("return"), button -> onClose())
                .bounds(width / 2 - 108, height - 28, 104, 20).build());
        addRenderableWidget(Button.builder(text("dismiss"), button -> {
            KeyGuideClient.clear();
            onClose();
        }).bounds(width / 2 + 4, height - 28, 104, 20).build());
        updateButtons();
    }

    private void changePage(int delta) {
        selected = null;
        heldModifier = InputConstants.UNKNOWN;
        page = Math.max(0, Math.min(pageCount() - 1, page + delta));
        rebuildWidgets();
    }

    private int pageCount() {
        return Math.max(1, (bindingIds.size() + pageSize - 1) / Math.max(1, pageSize));
    }

    private Component bindingLabel(KeyMapping mapping) {
        if (mapping == null) {
            return text("unavailable");
        }
        if (mapping == selected) {
            return text("waiting").withStyle(ChatFormatting.YELLOW);
        }
        Component label = mapping.getTranslatedKeyMessage();
        return conflicts(mapping).isEmpty() ? label : label.copy().withStyle(ChatFormatting.YELLOW);
    }

    private List<Component> conflicts(KeyMapping mapping) {
        List<Component> conflicts = new ArrayList<>();
        if (mapping != null && !mapping.isUnbound()) {
            for (KeyMapping other : minecraft.options.keyMappings) {
                if (other != mapping && !other.isUnbound() && mapping.same(other)) {
                    conflicts.add(Component.translatable(other.getName()));
                }
            }
        }
        return conflicts;
    }

    private void updateButtons() {
        for (Row row : rows) {
            KeyMapping mapping = row.mapping();
            row.binding().setMessage(bindingLabel(mapping));
            row.binding().active = mapping != null;
            row.reset().active = mapping != null && !mapping.isDefault();
            row.clear().active = mapping != null && !mapping.isUnbound();
            Component hint;
            if (mapping == null) {
                hint = Component.translatable("screen.smartkeyprompts.guide.missing_hint", row.id());
            } else {
                List<Component> shared = conflicts(mapping);
                hint = shared.isEmpty() ? Component.translatable(mapping.getName())
                        : Component.translatable("screen.smartkeyprompts.guide.conflict_hint",
                        shared.get(0), shared.size());
            }
            row.binding().setTooltip(Tooltip.create(hint));
        }
    }

    private void saveEdit() {
        selected = null;
        heldModifier = InputConstants.UNKNOWN;
        KeyMapping.resetMapping();
        KeyMapping.releaseAll();
        minecraft.options.save();
        HUD.clearCache();
        updateButtons();
    }

    private void capture(InputConstants.Key key) {
        ClientPlatform.get().setKey(selected, key, heldModifier);
        saveEdit();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (selected != null) {
            if (heldModifier.equals(InputConstants.UNKNOWN)) {
                int modifiers = (hasShiftDown() ? GLFW.GLFW_MOD_SHIFT : 0)
                        | (hasAltDown() ? GLFW.GLFW_MOD_ALT : 0)
                        | (hasControlDown() ? (Minecraft.ON_OSX ? GLFW.GLFW_MOD_SUPER : GLFW.GLFW_MOD_CONTROL) : 0);
                heldModifier = modifierFromMask(modifiers);
            }
            capture(InputConstants.Type.MOUSE.getOrCreate(button));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
        boolean modifierKey = ClientPlatform.get().isModifierKey(key);
        if (modifierKey) heldModifier = key;
        if (selected == null) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            selected = null;
            heldModifier = InputConstants.UNKNOWN;
            updateButtons();
            return true;
        }
        if (!modifierKey) {
            if (heldModifier.equals(InputConstants.UNKNOWN)) heldModifier = modifierFromMask(modifiers);
            capture(key);
        }
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (heldModifier.equals(InputConstants.getKey(keyCode, scanCode))) {
            InputConstants.Key modifierKey = heldModifier;
            heldModifier = InputConstants.UNKNOWN;
            if (selected != null) {
                capture(modifierKey);
                return true;
            }
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    private InputConstants.Key modifierFromMask(int modifiers) {
        int[] masks = {GLFW.GLFW_MOD_SHIFT, GLFW.GLFW_MOD_CONTROL, GLFW.GLFW_MOD_ALT, GLFW.GLFW_MOD_SUPER};
        int[] codes = {GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_LEFT_SUPER};
        for (int index = 0; index < masks.length; index++) {
            InputConstants.Key key = InputConstants.Type.KEYSYM.getOrCreate(codes[index]);
            if ((modifiers & masks[index]) != 0 && ClientPlatform.get().isModifierKey(key)) return key;
        }
        return InputConstants.UNKNOWN;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fillGradient(0, 0, width, height, 0xC0101010, 0xD0101010);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 12, 0xFFFFFF);
        Component instruction = selected == null ? text("hint")
                : Component.translatable("screen.smartkeyprompts.guide.capture_hint", Component.translatable(selected.getName()));
        int hintY = 29;
        for (var line : font.split(instruction, panelWidth)) {
            if (hintY > 49) {
                break;
            }
            graphics.drawCenteredString(font, line, width / 2, hintY, selected == null ? 0xBBBBBB : 0xFFFF55);
            hintY += font.lineHeight + 1;
        }
        for (Row row : rows) {
            Component action = Component.translatable(row.id());
            String label = font.plainSubstrByWidth(action.getString(), panelWidth - 190);
            graphics.drawString(font, label, panelX + 2, row.y() + 6, row.mapping() == null ? 0x999999 : 0xFFFFFF);
        }
        if (bindingIds.isEmpty()) {
            graphics.drawCenteredString(font, text("empty"), width / 2, ROW_TOP + 10, 0xBBBBBB);
        }
        graphics.drawCenteredString(font, text("saved"), width / 2, height - 54, 0xBBBBBB);
        if (pageCount() > 1) {
            graphics.drawCenteredString(font, Component.translatable("screen.smartkeyprompts.guide.page", page + 1, pageCount()),
                    width / 2, height - 42, 0xBBBBBB);
        }
    }

    private static net.minecraft.network.chat.MutableComponent text(String suffix) {
        return Component.translatable("screen.smartkeyprompts.guide." + suffix);
    }

    private record Row(String id, KeyMapping mapping, Button binding, Button reset, Button clear, int y) {
    }
}
