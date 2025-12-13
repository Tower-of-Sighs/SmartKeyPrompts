package com.mafuyu404.smartkeyprompts.init;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.util.Objects;

public class KeyDrawer {
    // 纹理资源位置
    private static final ResourceLocation TEXTURE_UNPRESSED =
            ResourceLocation.fromNamespaceAndPath(SmartKeyPrompts.MODID, "textures/gui/unpressed_key.png");
    private static final ResourceLocation TEXTURE_PRESSED =
            ResourceLocation.fromNamespaceAndPath(SmartKeyPrompts.MODID, "textures/gui/pressed_key.png");

    // 纹理尺寸
    public static final int TEXTURE_WIDTH = 32;
    public static final int TEXTURE_HEIGHT = 16;

    public static void drawUnpressedKey(PoseStack poseStack, int x, int y,
                                        int width, int height,
                                        int texX, int texY,
                                        int texWidth, int texHeight,
                                        boolean pressed) {
        // 设置渲染状态
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        if (pressed) {
            RenderSystem.setShaderTexture(0, TEXTURE_PRESSED);
        } else {
            RenderSystem.setShaderTexture(0, TEXTURE_UNPRESSED);
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // 创建变换矩阵
        poseStack.pushPose();
        poseStack.translate(x, y, 0);

        // 计算UV坐标
        float u0 = (float) texX / TEXTURE_WIDTH;
        float v0 = (float) texY / TEXTURE_HEIGHT;
        float u1 = (float) (texX + texWidth) / TEXTURE_WIDTH;
        float v1 = (float) (texY + texHeight) / TEXTURE_HEIGHT;

        // 构建顶点缓冲区
        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        Matrix4f matrix = poseStack.last().pose();
        buffer.addVertex(matrix, 0, height, 0).setUv(u0, v1);
        buffer.addVertex(matrix, width, height, 0).setUv(u1, v1);
        buffer.addVertex(matrix, width, 0, 0).setUv(u1, v0);
        buffer.addVertex(matrix, 0, 0, 0).setUv(u0, v0);

        // 绘制
        BufferUploader.drawWithShader(Objects.requireNonNull(buffer.build()));

        poseStack.popPose();
        RenderSystem.disableBlend();
    }
}
