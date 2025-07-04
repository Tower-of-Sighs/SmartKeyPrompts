package com.mafuyu404.smartkeyprompts.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class KeyRenderer {
    public static void drawKeyBoardKey(GuiGraphics guiGraphics, int x, int y, String key, boolean pressed) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        // 按键尺寸
        int width = font.width(key) + 4;
        int height = 12;

        int topColor    = 0xFF707070;  // 上亮面
        int faceColor   = 0xFF505050;  // 主体灰色
        int bottomColor = 0xFF202020;  // 下阴影

        if (pressed) {
            topColor = 0xFF505050;
            faceColor = 0xFF202020;
            bottomColor = 0xFF000000;
        }

        // 背景
        guiGraphics.fillGradient(x, y, x + width, y + height, topColor, bottomColor); // 垂直渐变背景
        guiGraphics.fill(x, y, x + width, y + height, faceColor); // 覆盖主色

        // 画边框
        guiGraphics.fill(x, y, x + width, y + 1, topColor); // 顶部线
        guiGraphics.fill(x, y, x + 1, y + height, topColor); // 左边线
        guiGraphics.fill(x, y + height - 1, x + width, y + height, bottomColor); // 底部线
        guiGraphics.fill(x + width - 1, y, x + width, y + height, bottomColor); // 右边线

        guiGraphics.drawCenteredString(font, key, x + width / 2, y + (height - 8) / 2, 0xFFFFFFFF);
    }

    public static void drawText(GuiGraphics guiGraphics, int x, int y, String text) {
        Font font = Minecraft.getInstance().font;
        // 渲染文字描边（四周偏移1像素）
        guiGraphics.drawString(
                font,
                text,
                x - 1, y,
                0xFF000000,
                true
        );
        guiGraphics.drawString(
                font,
                text,
                x + 1, y,
                0xFF000000,
                true
        );
        guiGraphics.drawString(
                font,
                text,
                x, y - 1,
                0xFF000000,
                true
        );
        guiGraphics.drawString(
                font,
                text,
                x, y + 1,
                0xFF000000,
                true
        );

        // 渲染主体文字
        guiGraphics.drawString(
                font,
                text,
                x, y,
                0xFFFFFFFF,
                true
        );
    }
}
