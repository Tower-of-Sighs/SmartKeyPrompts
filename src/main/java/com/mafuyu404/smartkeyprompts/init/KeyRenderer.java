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

        // 按键尺寸
        int width = font.width(key) + 4;
        int height = 12;

        int topColor = pressed ? 0xFF505050 : 0xFF707070;
        int faceColor = pressed ? 0xFF202020 : 0xFF505050;
        int bottomColor = pressed ? 0xFF000000 : 0xFF202020;

        guiGraphics.fill(x, y, x + width, y + height, faceColor);

        guiGraphics.fill(x, y, x + width, y + 1, topColor); // 顶部线
        guiGraphics.fill(x, y, x + 1, y + height, topColor); // 左边线
        guiGraphics.fill(x, y + height - 1, x + width, y + height, bottomColor); // 底部线
        guiGraphics.fill(x + width - 1, y, x + width, y + height, bottomColor); // 右边线

        guiGraphics.drawCenteredString(font, key, x + width / 2, y + (height - 8) / 2, 0xFFFFFFFF);
    }

    public static void drawText(GuiGraphics guiGraphics, int x, int y, String text) {
        Font font = getFont();

        guiGraphics.drawString(font, text, x, y, 0xFFFFFFFF, true);
    }
}
