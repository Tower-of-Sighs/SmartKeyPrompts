package com.mafuyu404.smartkeyprompts.event;

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

        int x = 10;
        int y = 10;
        for (KeyBindingInfo keyBindingInfo : bindingInfoList) {
            drawText(guiGraphics, x, y, "[" + keyBindingInfo.key() + "]" + keyBindingInfo.text(), 0.8f);
            y += 10;
        }
    }
    private static void drawText(GuiGraphics guiGraphics, int x, int y, String text, float scale) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose(); // 保存当前变换状态

        // 应用缩放变换（以指定位置为中心点）
        poseStack.translate(x, y, 0);
        poseStack.scale(scale, scale, 1.0f);
        poseStack.translate(-x, -y, 0);

        Font font = Minecraft.getInstance().font;
        // 渲染文字描边（四周偏移1像素）
        guiGraphics.drawString(
                font,
                text,
                x - 1, y,
                0xFF000000,
                false
        );
        guiGraphics.drawString(
                font,
                text,
                x + 1, y,
                0xFF000000,
                false
        );
        guiGraphics.drawString(
                font,
                text,
                x, y - 1,
                0xFF000000,
                false
        );
        guiGraphics.drawString(
                font,
                text,
                x, y + 1,
                0xFF000000,
                false
        );

        // 渲染主体文字
        guiGraphics.drawString(
                font,
                text,
                x, y,
                0xFFFFFFFF,
                false
        );

        poseStack.popPose();
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
