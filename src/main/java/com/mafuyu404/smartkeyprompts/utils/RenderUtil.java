package com.mafuyu404.smartkeyprompts.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RenderUtil {
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static Supplier<PlayerSkin> supplier = null;

    public static int screenWidth() {
        return minecraft.getWindow().getGuiScaledWidth();
    }

    public static int screenHeight() {
        return minecraft.getWindow().getGuiScaledHeight();
    }

    //fill

    //矩形
    public static void fillRect(GuiGraphics guiGraphics, int x, int y, int w, int h, int color) {
        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = guiGraphics.pose().last().pose();

        buf.addVertex(mat, (float) x, (float) y, 0).setColor(color);
        buf.addVertex(mat, (float) (x + w), (float) y, 0).setColor(color);
        buf.addVertex(mat, (float) (x + w), (float) (y + h), 0).setColor(color);
        buf.addVertex(mat, (float) x, (float) (y + h), 0).setColor(color);

        beginRendering();
        BufferUploader.drawWithShader(Objects.requireNonNull(buf.build()));
        finishRendering();
    }

    //圆弧
    public static void fillArc(GuiGraphics guiGraphics, int cX, int cY, int radius, int start, int end, int color) {
        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = guiGraphics.pose().last().pose();

        buf.addVertex(mat, (float) cX, (float) cY, 0).setColor(color);

        for (int i = start - 90; i <= end - 90; i++) {
            double angle = Math.toRadians(i);
            float x = (float) (Math.cos(angle) * radius) + cX;
            float y = (float) (Math.sin(angle) * radius) + cY;
            buf.addVertex(mat, x, y, 0).setColor(color);
        }

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    //圆
    public static void fillCircle(GuiGraphics guiGraphics, int cX, int cY, int radius, int color) {
        fillArc(guiGraphics, cX, cY, radius, 0, 360, color);
    }

    //环形扇区
    public static void fillAnnulusArc(GuiGraphics guiGraphics, int cx, int cy, int radius, int start, int end, int thickness, int color) {
        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = guiGraphics.pose().last().pose();

        for (int i = start - 90; i <= end - 90; i++) {
            float angle = (float) Math.toRadians(i);
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            float x1 = cx + cos * radius;
            float y1 = cy + sin * radius;
            float x2 = cx + cos * (radius + thickness);
            float y2 = cy + sin * (radius + thickness);
            buf.addVertex(mat, x1, y1, 0).setColor(color);
            buf.addVertex(mat, x2, y2, 0).setColor(color);
        }

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    //环形圆
    public static void fillAnnulus(GuiGraphics guiGraphics, int cx, int cy, int radius, int thickness, int color) {
        fillAnnulusArc(guiGraphics, cx, cy, radius, 0, 360, thickness, color);
    }

    //实心圆角矩形
    public static void fillRoundRect(GuiGraphics guiGraphics, int x, int y, int w, int h, int r, int color) {
        r = Mth.clamp(r, 0, Math.min(w, h) / 2);

        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = guiGraphics.pose().last().pose();

        buf.addVertex(mat, x + w / 2F, y + h / 2F, 0).setColor(color);

        int[][] corners = {
                {x + w - r, y + r},
                {x + w - r, y + h - r},
                {x + r, y + h - r},
                {x + r, y + r}
        };

        for (int corner = 0; corner < 4; corner++) {
            int cornerStart = (corner - 1) * 90;
            int cornerEnd = cornerStart + 90;
            for (int i = cornerStart; i <= cornerEnd; i += 10) {
                float angle = (float) Math.toRadians(i);
                float rx = corners[corner][0] + (float) (Math.cos(angle) * r);
                float ry = corners[corner][1] + (float) (Math.sin(angle) * r);
                buf.addVertex(mat, rx, ry, 0).setColor(color);
            }
        }

        buf.addVertex(mat, corners[0][0], y, 0).setColor(color);

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    //圆角阴影边框
    public static void fillRoundShadow(GuiGraphics guiGraphics, int x, int y, int w, int h, int r, int thickness, int innerColor, int outerColor) {
        r = Mth.clamp(r, 0, Math.min(w, h) / 2);

        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = guiGraphics.pose().last().pose();

        int[][] corners = {
                {x + w - r, y + r},
                {x + w - r, y + h - r},
                {x + r, y + h - r},
                {x + r, y + r}
        };

        for (int corner = 0; corner < 4; corner++) {
            int cornerStart = (corner - 1) * 90;
            int cornerEnd = cornerStart + 90;
            for (int i = cornerStart; i <= cornerEnd; i += 10) {
                float angle = (float) Math.toRadians(i);
                float rx1 = corners[corner][0] + (float) (Math.cos(angle) * r);
                float ry1 = corners[corner][1] + (float) (Math.sin(angle) * r);
                float rx2 = corners[corner][0] + (float) (Math.cos(angle) * (r + thickness));
                float ry2 = corners[corner][1] + (float) (Math.sin(angle) * (r + thickness));
                buf.addVertex(mat, rx1, ry1, 0).setColor(innerColor);
                buf.addVertex(mat, rx2, ry2, 0).setColor(outerColor);
            }
        }

        buf.addVertex(mat, corners[0][0], y, 0).setColor(innerColor);
        buf.addVertex(mat, corners[0][0], y - thickness, 0).setColor(outerColor);

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    //上圆角矩形
    public static void fillRoundTabTop(GuiGraphics guiGraphics, int x, int y, int w, int h, int r, int color) {
        r = Mth.clamp(r, 0, Math.min(w, h) / 2);

        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = guiGraphics.pose().last().pose();

        buf.addVertex(mat, x + w / 2F, y + h / 2F, 0).setColor(color);

        int[][] corners = {
                {x + r, y + r},
                {x + w - r, y + r}
        };

        for (int corner = 0; corner < 2; corner++) {
            int cornerStart = (corner - 2) * 90;
            int cornerEnd = cornerStart + 90;
            for (int i = cornerStart; i <= cornerEnd; i += 10) {
                float angle = (float) Math.toRadians(i);
                float rx = corners[corner][0] + (float) (Math.cos(angle) * r);
                float ry = corners[corner][1] + (float) (Math.sin(angle) * r);
                buf.addVertex(mat, rx, ry, 0).setColor(color);
            }
        }

        buf.addVertex(mat, x + w, y + h, 0).setColor(color);
        buf.addVertex(mat, x, y + h, 0).setColor(color);
        buf.addVertex(mat, x, corners[0][1], 0).setColor(color); // connect last to first vertex

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    //下圆角矩形
    public static void fillRoundTabBottom(GuiGraphics guiGraphics, int x, int y, int w, int h, int r, int color) {
        r = Mth.clamp(r, 0, Math.min(w, h) / 2);

        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = guiGraphics.pose().last().pose();

        buf.addVertex(mat, x + w / 2F, y + h / 2F, 0).setColor(color);

        int[][] corners = {
                {x + w - r, y + h - r},
                {x + r, y + h - r}
        };

        for (int corner = 0; corner < 2; corner++) {
            int cornerStart = corner * 90;
            int cornerEnd = cornerStart + 90;
            for (int i = cornerStart; i <= cornerEnd; i += 10) {
                float angle = (float) Math.toRadians(i);
                float rx = corners[corner][0] + (float) (Math.cos(angle) * r);
                float ry = corners[corner][1] + (float) (Math.sin(angle) * r);
                buf.addVertex(mat, rx, ry, 0).setColor(color);
            }
        }

        buf.addVertex(mat, x, y, 0).setColor(color);
        buf.addVertex(mat, x + w, y, 0).setColor(color);
        buf.addVertex(mat, x + w, corners[0][1], 0).setColor(color); // connect last to first vertex

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    //水平方向的胶囊状线条
    public static void fillRoundHorLine(GuiGraphics guiGraphics, int x, int y, int length, int thickness, int color) {
        fillRoundRect(guiGraphics, x, y, length, thickness, thickness / 2, color);
    }

    //垂直方向的胶囊状线条
    public static void fillRoundVerLine(GuiGraphics guiGraphics, int x, int y, int length, int thickness, int color) {
        fillRoundRect(guiGraphics, x, y, thickness, length, thickness / 2, color);
    }

    //draw

    //矩形
    public static void drawRect(GuiGraphics guiGraphics, int x, int y, int w, int h, int color) {
        drawHorLine(guiGraphics, x, y, w, color);
        drawVerLine(guiGraphics, x, y + 1, h - 2, color);
        drawVerLine(guiGraphics, x + w - 1, y + 1, h - 2, color);
        drawHorLine(guiGraphics, x, y + h - 1, w, color);
    }

    //盒子
    public static void drawBox(GuiGraphics guiGraphics, int x, int y, int w, int h, int color) {
        drawLine(guiGraphics, x, y, x + w, y, color);
        drawLine(guiGraphics, x, y + h, x + w, y + h, color);
        drawLine(guiGraphics, x, y, x, y + h, color);
        drawLine(guiGraphics, x + w, y, x + w, y + h, color);
    }

    //横线
    public static void drawHorLine(GuiGraphics guiGraphics, int x, int y, int length, int color) {
        fillRect(guiGraphics, x, y, length, 1, color);
    }

    //竖线
    public static void drawVerLine(GuiGraphics guiGraphics, int x, int y, int length, int color) {
        fillRect(guiGraphics, x, y, 1, length, color);
    }

    //一条线
    public static void drawLine(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int color) {
        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = guiGraphics.pose().last().pose();

        buf.addVertex(mat, (float) x1, (float) y1, 0).setColor(color);
        buf.addVertex(mat, (float) x2, (float) y2, 0).setColor(color);

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }


    //扇形
    public static void drawArc(GuiGraphics guiGraphics, int cX, int cY, int radius, int start, int end, int color) {
        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = guiGraphics.pose().last().pose();

        for (int i = start - 90; i <= end - 90; i++) {
            double angle = Math.toRadians(i);
            float x = (float) (Math.cos(angle) * radius) + cX;
            float y = (float) (Math.sin(angle) * radius) + cY;
            buf.addVertex(mat, x, y, 0).setColor(color);
        }

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    //圆
    public static void drawCircle(GuiGraphics guiGraphics, int cX, int cY, int radius, int color) {
        drawArc(guiGraphics, cX, cY, radius, 0, 360, color);
    }

    //圆角矩形
    public static void drawRoundRect(GuiGraphics guiGraphics, int x, int y, int w, int h, int r, int color) {
        r = Mth.clamp(r, 0, Math.min(w, h) / 2);

        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = guiGraphics.pose().last().pose();

        int[][] corners = {
                {x + w - r, y + r},
                {x + w - r, y + h - r},
                {x + r, y + h - r},
                {x + r, y + r}
        };

        for (int corner = 0; corner < 4; corner++) {
            int cornerStart = (corner - 1) * 90;
            int cornerEnd = cornerStart + 90;
            for (int i = cornerStart; i <= cornerEnd; i += 10) {
                float angle = (float) Math.toRadians(i);
                float rx = corners[corner][0] + (float) (Math.cos(angle) * r);
                float ry = corners[corner][1] + (float) (Math.sin(angle) * r);
                buf.addVertex(mat, rx, ry, 0).setColor(color);
            }
        }

        buf.addVertex(mat, corners[0][0], y, 0).setColor(color); // connect last to first vertex

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    //圆角横线
    public static void drawRoundHorLine(GuiGraphics guiGraphics, int x, int y, int length, int thickness, int color) {
        drawRoundRect(guiGraphics, x, y, length, thickness, thickness / 2, color);
    }

    //圆角竖线
    public static void drawRoundVerLine(GuiGraphics guiGraphics, int x, int y, int length, int thickness, int color) {
        drawRoundRect(guiGraphics, x, y, thickness, length, thickness / 2, color);
    }

    // image
    public static void renderImage(GuiGraphics guiGraphics, ResourceLocation resourceLocation, int x, int y, int z, int uw, int uh, int width, int height) {
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        bufferBuilder.addVertex(matrix4f, x, y, z).setUv(0, 0);
        bufferBuilder.addVertex(matrix4f, x, y + height, z).setUv(0, uh);
        bufferBuilder.addVertex(matrix4f, x + width, y + height, z).setUv(uw, uh);
        bufferBuilder.addVertex(matrix4f, x + width, y, z).setUv(uw, 0);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, resourceLocation);
        RenderSystem.enableBlend();
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }

    public static void renderImage(GuiGraphics guiGraphics, ResourceLocation resourceLocation, int x, int y, int z, int width, int height, float scale) {
        x = (int) (x / scale);
        y = (int) (y / scale);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, scale);
        renderImage(guiGraphics, resourceLocation, x, y, z, 1, 1, width, height);
        guiGraphics.pose().popPose();
    }

    public static void renderPlayerHead(GuiGraphics guiGraphics, String input, int x, int y, int size, float scale) {
        handleGameProfileAsync(input, gameProfile -> supplier = RenderUtil.texturesSupplier(gameProfile));
        x = (int) (x / scale);
        y = (int) (y / scale);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, scale);
        PlayerFaceRenderer.draw(guiGraphics, supplier == null ? DefaultPlayerSkin.getDefaultTexture() : supplier.get().texture(), x, y, size);
        guiGraphics.pose().popPose();
    }

    public static void renderItem(GuiGraphics guiGraphics, ItemStack item, int x, int y, float scale, String text) {
        x = (int) (x / scale);
        y = (int) (y / scale);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, scale);
        guiGraphics.renderItem(item, x, y);
        guiGraphics.renderItemDecorations(minecraft.font, item, x, y, text);
        guiGraphics.pose().popPose();
    }

    public static void renderItem(GuiGraphics guiGraphics, ItemStack item, int x, int y, float scale) {
        renderItem(guiGraphics, item, x, y, scale, "");
    }

    // text
    public static void drawLeftScaleText(GuiGraphics guiGraphics, Component component, int x, int y, float scale, boolean shadow, int color) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.scale(scale, scale, scale);

        float rescale = 1 / scale;
        x = (int) (x * rescale);
        y = (int) (y * rescale);

        guiGraphics.drawString(minecraft.font, component, x, y, color, shadow);
        poseStack.scale(rescale, rescale, rescale);
    }

    public static void drawCenterScaleText(GuiGraphics guiGraphics, Component component, int centerX, int y, float scale, boolean shadow, int color) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.scale(scale, scale, scale);

        float rescale = 1 / scale;
        centerX = (int) (centerX * rescale);
        centerX = centerX - (minecraft.font.width(component) / 2);
        y = (int) (y * rescale);

        guiGraphics.drawString(minecraft.font, component, centerX, y, color, shadow);
        poseStack.scale(rescale, rescale, rescale);
    }

    public static void drawRightScaleText(GuiGraphics guiGraphics, Component component, int rightX, int y, float scale, boolean shadow, int color) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.scale(scale, scale, scale);

        float rescale = 1 / scale;
        rightX = (int) (rightX * rescale);
        rightX = rightX - minecraft.font.width(component);
        y = (int) (y * rescale);

        guiGraphics.drawString(minecraft.font, component, rightX, y, color, shadow);
        poseStack.scale(rescale, rescale, rescale);
    }

    //cursor

    public static void setCursor(int x, int y) {
        Window window = minecraft.getWindow();
        int w1 = window.getWidth();
        int w2 = screenWidth();
        int h1 = window.getHeight();
        int h2 = screenHeight();
        double ratW = (double) w2 / (double) w1;
        double ratH = (double) h2 / (double) h1;
        GLFW.glfwSetCursorPos(window.getWindow(), x / ratW, y / ratH);
    }

    public static Point getCursor() {
        Window window = minecraft.getWindow();
        int w1 = window.getWidth();
        int w2 = screenWidth();
        int h1 = window.getHeight();
        int h2 = screenHeight();
        double rW = (double) w2 / (double) w1;
        double rH = (double) h2 / (double) h1;
        return new Point((int) (rW * minecraft.mouseHandler.xpos()), (int) (rH * minecraft.mouseHandler.ypos()));
    }

    //util

    public static void renderOpacity(GuiGraphics guiGraphics, float opacity, Runnable runnable) {
        RenderSystem.enableBlend();
        guiGraphics.setColor(1f, 1f, 1f, opacity);
        runnable.run();
        RenderSystem.disableBlend();
    }

    //private

    private static void drawBuffer(BufferBuilder buf) {
        BufferUploader.drawWithShader(Objects.requireNonNull(buf.build()));
    }

    public static void beginRendering() {
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
    }

    public static void finishRendering() {
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }

    private static Tesselator getTesselator() {
        return Tesselator.getInstance();
    }

    private static void handleGameProfileAsync(String input, Consumer<GameProfile> postAction) {
        ResolvableProfile component = createProfileComponent(input);
        component.resolve()
                .thenApplyAsync(result -> {
                    GameProfile profile = result.gameProfile();
                    postAction.accept(profile);
                    return profile;
                })
                .exceptionally(ex -> null);
    }

    private static ResolvableProfile createProfileComponent(String input) {
        try {
            UUID uuid = UUID.fromString(input);
            return new ResolvableProfile(Optional.empty(), Optional.of(uuid), new PropertyMap());
        } catch (IllegalArgumentException e) {
            return new ResolvableProfile(Optional.of(input), Optional.empty(), new PropertyMap());
        }
    }

    private static Supplier<PlayerSkin> texturesSupplier(GameProfile profile) {
        Minecraft minecraft = Minecraft.getInstance();
        SkinManager skinManager = minecraft.getSkinManager();
        CompletableFuture<PlayerSkin> completableFuture = skinManager.getOrLoad(profile);
        boolean bl = !minecraft.isLocalPlayer(profile.getId());
        PlayerSkin playerSkin = DefaultPlayerSkin.get(profile);
        return () -> {
            PlayerSkin PlayerSkin2 = completableFuture.getNow(playerSkin);
            return bl && !PlayerSkin2.secure() ? playerSkin : PlayerSkin2;
        };
    }
}
