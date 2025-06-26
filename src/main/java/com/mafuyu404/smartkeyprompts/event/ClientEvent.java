package com.mafuyu404.smartkeyprompts.event;

import com.mafuyu404.smartkeyprompts.Config;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
public class ClientEvent {
    public static ArrayList<KeyBindingInfo> bindingInfoList = new ArrayList<>();
    private static Font font;

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().player == null) return;
        if (event.phase == TickEvent.Phase.START) {
            SmartKeyPrompts.show(new String[]{"key.jade.narrate", "key.sprint", "key.jei.cheatOneItem"});
            if (Minecraft.getInstance().player.isSprinting()) {
                SmartKeyPrompts.custom(new String[]{"key.keyboard.space"}, new String[]{"飞跃"});
            }
        }
    }
    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGuiOverlayEvent.Post event) {
        if (Minecraft.getInstance().screen != null) return;
        if (event.getOverlay().id().equals(VanillaGuiOverlay.DEBUG_TEXT.id())) {
            drawHud(event.getGuiGraphics());
        }
    }
    @SubscribeEvent
    public static void onRenderScreenOverlay(ScreenEvent.Render.Post event) {
        if (Minecraft.getInstance().player == null) return;
        drawHud(event.getGuiGraphics());
    }

    private static void drawHud(GuiGraphics guiGraphics) {
        Window window = Minecraft.getInstance().getWindow();
        int screenWidth = window.getGuiScaledWidth();
        int screenHeight = window.getGuiScaledHeight();

        if (font == null) font = Minecraft.getInstance().font;

        float scale = Config.SCALE.get().floatValue();
        int position = Config.POSITION.get();
        int x = 0, y = 0;

        if (position == 1 || position == 7 || position == 8) {
            x = 5;
        }
        if (position == 2 || position == 6) {
            x = screenWidth / 2;
        }
        if (position == 3 || position == 4 || position == 5) {
            x = screenWidth - 5;
        }
        if (position == 1 || position == 2 || position == 3) {
            y = 5;
        }
        if (position == 4 || position == 8) {
            y = screenHeight / 2;
        }
        if (position == 5 || position == 6 || position == 7) {
            y = screenHeight - 5;
        }

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose(); // 保存当前变换状态

        // 应用缩放变换（以指定位置为中心点）
        poseStack.translate(x, y, 0);
        poseStack.scale(scale, scale, 1.0f);
        poseStack.translate(-x, -y, 0);

        for (KeyBindingInfo keyBindingInfo : bindingInfoList) {
            KeyRenderer.drawKeyBoardKey(guiGraphics, x, y, keyBindingInfo.key());
            drawText(guiGraphics, font.width(keyBindingInfo.key()) + 12, y + 2, keyBindingInfo.text());
            y += 14;
        }

        poseStack.popPose();
    }
    public static void drawText(GuiGraphics guiGraphics, int x, int y, String text) {

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

    public static ArrayList<KeyBindingInfo> getAllKeyBindings() {
        Minecraft mc = Minecraft.getInstance();
        ArrayList<KeyBindingInfo> bindingList = new ArrayList<>();
//        Map<String, List<KeyBindingInfo>> groupedBindings = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (KeyMapping binding : mc.options.keyMappings) {
            KeyBindingInfo info = new KeyBindingInfo(
                    binding.getName(),
                    Component.translatable(binding.getName()).getString(),
                    binding.getKey().getDisplayName().getString()
            );
//            groupedBindings
//                    .computeIfAbsent(binding.getCategory(), k -> new ArrayList<>())
//                    .add(info);
            bindingList.add(info);
        }
        return bindingList;
    }

    // 按键绑定信息类
    public record KeyBindingInfo(String name, String text, String key) {}
}
