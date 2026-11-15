package com.mafuyu404.smartkeyprompts.init;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class KeyRenderer {
    private static Font cachedFont;

    private static Font getFont() {
        if (cachedFont == null) {
            cachedFont = Minecraft.getInstance().font;
        }
        return cachedFont;
    }

    public static void drawKeyBoardKey(GuiGraphics guiGraphics, int x, int y, String key, boolean pressed) {
        Font font = getFont();

        int width = font.width(key);
        int height = 12;

        PoseStack poseStack = guiGraphics.pose();

        KeyDrawer.drawUnpressedKey(poseStack,
                x, y,
                6, KeyDrawer.TEXTURE_HEIGHT,
                0, 0,
                6, KeyDrawer.TEXTURE_HEIGHT, pressed);
        KeyDrawer.drawUnpressedKey(poseStack,
                x + 6, y,
                width - 1, KeyDrawer.TEXTURE_HEIGHT,
                6, 0,
                20, KeyDrawer.TEXTURE_HEIGHT, pressed);
        KeyDrawer.drawUnpressedKey(poseStack,
                x + 6 + width - 1, y,
                6, KeyDrawer.TEXTURE_HEIGHT,
                26, 0,
                6, KeyDrawer.TEXTURE_HEIGHT, pressed);

        guiGraphics.drawCenteredString(font, key, x + width / 2 + 6, y + (height - 8) / 2 + 1, 0xFFFFFFFF);
    }

    public static void drawText(GuiGraphics guiGraphics, int x, int y, String text) {
        Font font = getFont();

        guiGraphics.drawString(font, text, x, y, 0xFFFFFFFF, true);
    }
}
