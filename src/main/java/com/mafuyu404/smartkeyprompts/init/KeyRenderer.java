package com.mafuyu404.smartkeyprompts.init;

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

        KeyDrawer.drawImage(guiGraphics.pose(),
                x, y,
                6, KeyDrawer.TEXTURE_HEIGHT,
                0, 0,
                6, KeyDrawer.TEXTURE_HEIGHT);
        KeyDrawer.drawImage(guiGraphics.pose(),
                x + 6, y,
                width - 1, KeyDrawer.TEXTURE_HEIGHT,
                6, 0,
                20, KeyDrawer.TEXTURE_HEIGHT);
        KeyDrawer.drawImage(guiGraphics.pose(),
                x + 6 + width - 1, y,
                6, KeyDrawer.TEXTURE_HEIGHT,
                26, 0,
                6, KeyDrawer.TEXTURE_HEIGHT);

        guiGraphics.drawCenteredString(font, key, x + width / 2 + 6, y + (height - 8) / 2 + 1, 0xFFFFFFFF);
    }

    public static void drawImage(int x, int y, String key, boolean pressed) {
        Font font = getFont();

        // 按键尺寸
        int width = font.width(key) + 4;
        int height = 12;

        int topColor = pressed ? 0xFF505050 : 0xFF707070;
        int faceColor = pressed ? 0xFF202020 : 0xFF505050;
        int bottomColor = pressed ? 0xFF000000 : 0xFF202020;
    }

    public static void drawText(GuiGraphics guiGraphics, int x, int y, String text) {
        Font font = getFont();

        guiGraphics.drawString(font, text, x, y, 0xFFFFFFFF, true);
    }
}
