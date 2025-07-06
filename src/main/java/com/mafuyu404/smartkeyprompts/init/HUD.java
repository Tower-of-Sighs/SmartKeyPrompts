package com.mafuyu404.smartkeyprompts.init;

import com.mafuyu404.smartkeyprompts.Config;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

import static com.mafuyu404.smartkeyprompts.SmartKeyPrompts.MODID;
import static com.mafuyu404.smartkeyprompts.init.Utils.translateKey;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class HUD {
    public static List<KeyPrompt> KeyPromptList = new ArrayList<>();
    public static List<KeyPrompt> KeyPromptCache = new ArrayList<>();
    private static Font font;
    public static KeyMapping[] KeyMappingCache;

    public static void addCache(KeyPrompt keyPrompt) {
//        KeyPromptCache.stream().map(KeyPrompt::getString).toList().contains(keyPrompt.getString())
        if (!KeyPromptCache.stream().map(KeyPrompt::getString).toList().contains(keyPrompt.getString())) {
            KeyPromptCache.add(keyPrompt);
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;
        if (event.phase == TickEvent.Phase.START) {
            if (!Utils.isKeyPressed(ModKeybindings.CONTROL_KEY.getKey().getValue())) {
                List<? extends String> blacklist = Config.BLACKLIST.get();
                KeyPromptList.clear();
                KeyPromptCache.forEach(keyPrompt -> {
                    if (!blacklist.contains(keyPrompt.group)) {
                        KeyPromptList.add(keyPrompt);
                    }
                });
            } else {
                List.of(
                        new KeyPrompt(MODID, "key.mouse.left", "key.smartkeyprompts.keybinding", true),
                        new KeyPrompt(MODID, "key.mouse.wheel", "key.smartkeyprompts.scale", true),
                        new KeyPrompt(MODID, "key.mouse.right", "key.smartkeyprompts.position", true)
                ).forEach(keyPrompt -> {
                    if (!KeyPromptList.stream().map(KeyPrompt::getString).toList().contains(keyPrompt.getString())) KeyPromptList.add(keyPrompt);
                });
            }
            KeyPromptCache.clear();
        }
        if (!(minecraft.screen instanceof KeyBindsScreen) && KeyMappingCache != null) {
            minecraft.options.keyMappings = KeyMappingCache;
            KeyMappingCache = null;
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
        int y0 = 0;

        if (position == 2 || position == 6) {
            return;
        }
        if (position == 1 || position == 3) {
            y0 = 5;
        }
        if (position == 4 || position == 8) {
            int totalHeight = KeyPromptList.size() * 14;
            y0 = screenHeight / 2 - totalHeight / 2;
        }
        if (position == 5 || position == 7) {
            y0 = screenHeight - 5 - KeyPromptList.size() * 14;
        }

        PoseStack poseStack = guiGraphics.pose();

        List<KeyPrompt> defaultKeyPrompts = KeyPromptList.stream().filter(keyPrompt -> keyPrompt.position.equals("default")).toList();

        for (int i = 0; i < defaultKeyPrompts.size(); i++) {
            KeyPrompt keyPrompt = defaultKeyPrompts.get(i);
            poseStack.pushPose();

            String key = translateKey(keyPrompt.key);
            String desc = Component.translatable(keyPrompt.desc).getString();
            boolean pressed = Utils.isKeyPressedOfDesc(keyPrompt.desc);

            int x, y = y0 + (int) (16.0 * scale * i);

            if (position == 1 || position == 7 || position == 8) {
                x = 5;
                scaleHUD(poseStack, x, y, scale);
                KeyRenderer.drawKeyBoardKey(guiGraphics, x, y, key, pressed);
                KeyRenderer.drawText(guiGraphics, x + font.width(key) + 7, y + 2, desc);
            }
            if (position == 3 || position == 4 || position == 5) {
                x = screenWidth - 8;
                scaleHUD(poseStack, x, y, scale);
                KeyRenderer.drawText(guiGraphics, x - font.width(desc + key) - 3, y + 2, desc);
                KeyRenderer.drawKeyBoardKey(guiGraphics, x - font.width(key), y, key, pressed);
            }

            poseStack.popPose();
        }

        List<KeyPrompt> crosshairKeyPrompts = KeyPromptList.stream().filter(keyPrompt -> keyPrompt.position.equals("crosshair")).toList();

        for (int i = 0; i < crosshairKeyPrompts.size(); i++) {
            KeyPrompt keyPrompt = crosshairKeyPrompts.get(i);
            poseStack.pushPose();

            String key = translateKey(keyPrompt.key);
            String desc = Component.translatable(keyPrompt.desc).getString();
            boolean pressed = Utils.isKeyPressedOfDesc(keyPrompt.desc);

            int x = screenWidth / 2 - (int) (font.width(key + " : " + desc) * scale / 2);
            int y = screenHeight / 2 + 5 + (int) (16.0 * scale * i);

            scaleHUD(poseStack, x, y, scale);
            KeyRenderer.drawKeyBoardKey(guiGraphics, x, y, key, pressed);
            KeyRenderer.drawText(guiGraphics, x + font.width(key) + 7, y + 2, desc);

            poseStack.popPose();
        }
    }
    private static void scaleHUD(PoseStack poseStack, int x, int y, float scale) {
        poseStack.translate(x, y, 0);
        poseStack.scale(scale, scale, 1.0f);
        poseStack.translate(-x, -y, 0);
    }
}