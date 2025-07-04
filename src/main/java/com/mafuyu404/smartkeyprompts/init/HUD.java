package com.mafuyu404.smartkeyprompts.init;

import com.mafuyu404.smartkeyprompts.ModConfig;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

import static com.mafuyu404.smartkeyprompts.SmartKeyPrompts.MODID;
import static com.mafuyu404.smartkeyprompts.init.Utils.translateKey;


public class HUD {
    public static List<KeyBindingInfo> bindingInfoList = new ArrayList<>();
    public static List<KeyBindingInfo> bindingInfoCache = new ArrayList<>();
    private static Font font;
    public static KeyMapping[] KeyMappingCache;

    public static void addCache(KeyBindingInfo keyBindingInfo) {
        if (!bindingInfoCache.contains(keyBindingInfo)) {
            bindingInfoCache.add(keyBindingInfo);
        }
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
//            System.out.print(Utils.getTargetedEntity() + "\n");

            if (!Utils.isKeyPressed(ModKeybindings.CONTROL_KEY.key.getValue())) {
                List<String> blacklist = ModConfig.getInstance().blacklist;
                bindingInfoList.clear();
                bindingInfoCache.forEach(keyBindingInfo -> {
                    if (!blacklist.contains(keyBindingInfo.id())) {
                        bindingInfoList.add(keyBindingInfo);
                    }
                });
            } else {
                List.of(
                        new HUD.KeyBindingInfo(MODID, "key.mouse.left", "key.smartkeyprompts.keybinding", true),
                        new HUD.KeyBindingInfo(MODID, "key.mouse.wheel", "key.smartkeyprompts.scale", true),
                        new HUD.KeyBindingInfo(MODID, "key.mouse.right", "key.smartkeyprompts.position", true)
                ).forEach(keyBindingInfo -> {
                    if (!bindingInfoList.contains(keyBindingInfo)) bindingInfoList.add(keyBindingInfo);
                });
            }
            bindingInfoCache.clear();

            if (!(client.screen instanceof KeyBindsScreen) && KeyMappingCache != null) {
                client.options.keyMappings = KeyMappingCache;
                KeyMappingCache = null;
            }
        });

        HudRenderCallback.EVENT.register((guiGraphics, tickDelta) -> {
            if (Minecraft.getInstance().screen != null) return;
            drawHud(guiGraphics);
        });
    }

    private static void drawHud(GuiGraphics guiGraphics) {
        Window window = Minecraft.getInstance().getWindow();
        int screenWidth = window.getGuiScaledWidth();
        int screenHeight = window.getGuiScaledHeight();

        if (font == null) font = Minecraft.getInstance().font;

        float scale = (float) ModConfig.getInstance().scale;
        int position = ModConfig.getInstance().position;
        int x = 0, y = 0;

        if (position == 2 || position == 6) {
            return;
        }
        if (position == 1 || position == 3) {
            y = 5;
        }
        if (position == 4 || position == 8) {
            int totalHeight = bindingInfoList.size() * 14;
            y = screenHeight / 2 - totalHeight / 2;
        }
        if (position == 5 || position == 7) {
            y = screenHeight - 5 - bindingInfoList.size() * 14;
        }

        PoseStack poseStack = guiGraphics.pose();

        for (int i = 0; i < bindingInfoList.size(); i++) {
            KeyBindingInfo keyBindingInfo = bindingInfoList.get(i);
            poseStack.pushPose();

            String key = translateKey(keyBindingInfo.key);
            String desc = Component.translatable(keyBindingInfo.desc).getString();
            boolean pressed = Utils.isKeyPressedOfDesc(keyBindingInfo.desc);

            if (i != 0) y += (int) (16.0 * scale);

            if (position == 1 || position == 7 || position == 8) {
                x = 5;
                poseStack.translate(x, y, 0);
                poseStack.scale(scale, scale, 1.0f);
                poseStack.translate(-x, -y, 0);
                KeyRenderer.drawKeyBoardKey(guiGraphics, x, y, key, pressed);
                KeyRenderer.drawText(guiGraphics, x + font.width(key) + 7, y + 2, desc);
            }
            if (position == 3 || position == 4 || position == 5) {
                x = screenWidth - 8;
                poseStack.translate(x, y, 0);
                poseStack.scale(scale, scale, 1.0f);
                poseStack.translate(-x, -y, 0);
                KeyRenderer.drawText(guiGraphics, x - font.width(desc + key) - 3, y + 2, desc);
                KeyRenderer.drawKeyBoardKey(guiGraphics, x - font.width(key), y, key, pressed);
            }

            poseStack.popPose();
        }
    }

    // 按键绑定信息类
    public record KeyBindingInfo(String id, String key, String desc, boolean custom) {
    }
}