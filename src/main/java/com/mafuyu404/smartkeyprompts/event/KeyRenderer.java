package com.mafuyu404.smartkeyprompts.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

import static com.mafuyu404.smartkeyprompts.event.ClientEvent.drawText;

public class KeyRenderer {
    public static void drawKeyBoardKey(GuiGraphics guiGraphics, int x, int y, String key) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        // 按键尺寸
        int width = font.width(key) + 4;
        int height = 12;
        int cornerRadius = 4;

        // 渐变/阴影色：用于制造3D边缘
        int topColor    = 0xFF707070;  // 上亮面 — 深灰
        int faceColor   = 0xFF505050;  // 主体灰色 — 更暗
        int bottomColor = 0xFF202020;  // 下阴影 — 深沉阴影
        int outlineColor= 0xFF202020;  // 外边框色 — 几乎黑

        // 背景（阴影模拟 3D）
        guiGraphics.fillGradient(x, y, x + width, y + height, topColor, bottomColor); // 垂直渐变背景
        guiGraphics.fill(x, y, x + width, y + height, faceColor); // 覆盖主色（可用透明模拟高光）

        // 画边框：模拟立体边缘（手动模拟边缘阴影）
        guiGraphics.fill(x, y, x + width, y + 1, topColor); // 顶部线
        guiGraphics.fill(x, y, x + 1, y + height, topColor); // 左边线
        guiGraphics.fill(x, y + height - 1, x + width, y + height, bottomColor); // 底部线
        guiGraphics.fill(x + width - 1, y, x + width, y + height, bottomColor); // 右边线

        // 可选：外框（更突出轮廓）
        guiGraphics.drawCenteredString(font, key, x + width / 2, y + (height - 8) / 2, 0xFFFFFFFF);
    }
}
