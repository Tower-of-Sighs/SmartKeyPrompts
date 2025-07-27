package com.mafuyu404.smartkeyprompts.init;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.joml.Matrix4f;

public class KeyDrawer {
    // 纹理资源位置
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(SmartKeyPrompts.MODID, "textures/gui/key.png");

    // 纹理尺寸
    public static final int TEXTURE_WIDTH = 32;
    public static final int TEXTURE_HEIGHT = 16;

    /**
     * 在指定位置渲染图像
     * @param poseStack 渲染堆栈
     * @param x 屏幕X坐标
     * @param y 屏幕Y坐标
     */
    public static void drawImage(PoseStack poseStack, int x, int y) {
        drawImage(poseStack, x, y, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    /**
     * 在指定位置渲染图像（自定义尺寸）
     * @param poseStack 渲染堆栈
     * @param x 屏幕X坐标
     * @param y 屏幕Y坐标
     * @param width 渲染宽度
     * @param height 渲染高度
     */
    public static void drawImage(PoseStack poseStack, int x, int y, int width, int height) {
        drawImage(poseStack, x, y, width, height, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    /**
     * 渲染图像的高级方法
     * @param poseStack 渲染堆栈
     * @param x 屏幕X坐标
     * @param y 屏幕Y坐标
     * @param width 渲染宽度
     * @param height 渲染高度
     * @param texX 纹理X偏移
     * @param texY 纹理Y偏移
     * @param texWidth 纹理宽度
     * @param texHeight 纹理高度
     */
    public static void drawImage(PoseStack poseStack, int x, int y,
                                 int width, int height,
                                 int texX, int texY,
                                 int texWidth, int texHeight) {
        // 设置渲染状态
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
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
        var buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        Matrix4f matrix = poseStack.last().pose();
        buffer.vertex(matrix, 0, height, 0).uv(u0, v1).endVertex();
        buffer.vertex(matrix, width, height, 0).uv(u1, v1).endVertex();
        buffer.vertex(matrix, width, 0, 0).uv(u1, v0).endVertex();
        buffer.vertex(matrix, 0, 0, 0).uv(u0, v0).endVertex();

        // 绘制
        BufferUploader.drawWithShader(buffer.end());

        poseStack.popPose();
        RenderSystem.disableBlend();
    }

    /**
     * 带颜色的图像渲染
     * @param poseStack 渲染堆栈
     * @param x 屏幕X坐标
     * @param y 屏幕Y坐标
     * @param width 渲染宽度
     * @param height 渲染高度
     * @param color RGBA颜色（0xAARRGGBB格式）
     */
    public static void drawImage(PoseStack poseStack, int x, int y,
                                 int width, int height, int color) {
        // 提取颜色分量
        float a = FastColor.ARGB32.alpha(color) / 255.0F;
        float r = FastColor.ARGB32.red(color) / 255.0F;
        float g = FastColor.ARGB32.green(color) / 255.0F;
        float b = FastColor.ARGB32.blue(color) / 255.0F;

        // 设置渲染状态
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(r, g, b, a);

        // 创建变换矩阵
        poseStack.pushPose();
        poseStack.translate(x, y, 0);

        // UV坐标（使用整个纹理）
        float u0 = 0;
        float v0 = 0;
        float u1 = 1;
        float v1 = 1;

        // 构建顶点缓冲区
        var buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        Matrix4f matrix = poseStack.last().pose();
        buffer.vertex(matrix, 0, height, 0).uv(u0, v1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, width, height, 0).uv(u1, v1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, width, 0, 0).uv(u1, v0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, 0, 0, 0).uv(u0, v0).color(r, g, b, a).endVertex();

        // 绘制
        BufferUploader.drawWithShader(buffer.end());

        poseStack.popPose();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
