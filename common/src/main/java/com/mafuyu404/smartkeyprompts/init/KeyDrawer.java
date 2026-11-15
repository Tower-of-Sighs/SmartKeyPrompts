package com.mafuyu404.smartkeyprompts.init;

import com.mafuyu404.smartkeyprompts.platform.ClientPlatform;
import com.mojang.blaze3d.vertex.PoseStack;

/** Supplies shared key texture dimensions and delegates drawing to the target. */
public final class KeyDrawer {
    public static final int TEXTURE_WIDTH = 32;
    public static final int TEXTURE_HEIGHT = 16;

    public static void drawUnpressedKey(PoseStack poseStack, int x, int y,
                                       int width, int height, int texX, int texY,
                                       int texWidth, int texHeight, boolean pressed) {
        ClientPlatform.get().drawKeyTexture(poseStack, x, y, width, height,
                texX, texY, texWidth, texHeight, pressed);
    }
}
