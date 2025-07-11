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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mafuyu404.smartkeyprompts.SmartKeyPrompts.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class HUD {
    public static List<KeyPrompt> KeyPromptList = new ArrayList<>();
    public static List<KeyPrompt> KeyPromptCache = new ArrayList<>();
    private static Font font;
    public static KeyMapping[] KeyMappingCache;
    private static final Map<String, String> translationCache = new HashMap<>();
    private static final Map<String, String> keyTranslationCache = new HashMap<>();
    private static List<KeyPrompt> cachedDefaultPrompts = new ArrayList<>();
    private static List<KeyPrompt> cachedCrosshairPrompts = new ArrayList<>();
    private static int lastPromptListHash = 0;

    public static void addCache(KeyPrompt keyPrompt) {
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
                    if (!KeyPromptList.stream().map(KeyPrompt::getString).toList().contains(keyPrompt.getString()))
                        KeyPromptList.add(keyPrompt);
                });
            }
            KeyPromptCache.clear();

            // 更新缓存的提示列表
            updateCachedPrompts();
        }
        if (!(minecraft.screen instanceof KeyBindsScreen) && KeyMappingCache != null) {
            minecraft.options.keyMappings = KeyMappingCache;
            KeyMappingCache = null;
        }
    }

    private static void updateCachedPrompts() {
        int currentHash = KeyPromptList.hashCode();
        if (currentHash != lastPromptListHash) {
            cachedDefaultPrompts = KeyPromptList.stream()
                    .filter(keyPrompt -> keyPrompt.position.equals("default"))
                    .toList();
            cachedCrosshairPrompts = KeyPromptList.stream()
                    .filter(keyPrompt -> keyPrompt.position.equals("crosshair"))
                    .toList();
            lastPromptListHash = currentHash;
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
        if (KeyPromptList.isEmpty()) return;

        Window window = Minecraft.getInstance().getWindow();
        int screenWidth = window.getGuiScaledWidth();
        int screenHeight = window.getGuiScaledHeight();

        if (font == null) font = Minecraft.getInstance().font;

        float scale = Config.SCALE.get().floatValue();
        int position = Config.POSITION.get();

        if (position == 2 || position == 6) {
            return;
        }

        // 缓存计算结果
        int y0 = calculateY0(position, screenHeight, cachedDefaultPrompts.size());
        boolean isControlDown = ModKeybindings.CONTROL_KEY.isDown();

        PoseStack poseStack = guiGraphics.pose();

        // 渲染默认位置的提示
        renderKeyPrompts(guiGraphics, poseStack, cachedDefaultPrompts, position, screenWidth, y0, scale, isControlDown, false);

        // 渲染十字准星位置的提示
        renderKeyPrompts(guiGraphics, poseStack, cachedCrosshairPrompts, position, screenWidth, screenHeight / 2 + 7, scale, isControlDown, true);
    }

    private static int calculateY0(int position, int screenHeight, int promptCount) {
        return switch (position) {
            case 1, 3 -> 5;
            case 4, 8 -> screenHeight / 2 - (promptCount * 14) / 2;
            case 5, 7 -> screenHeight - 5 - promptCount * 14;
            default -> 0;
        };
    }

    private static void renderKeyPrompts(GuiGraphics guiGraphics, PoseStack poseStack, List<KeyPrompt> prompts,
                                         int position, int screenWidth, int baseY, float scale, boolean isControlDown, boolean isCrosshair) {
        for (int i = 0; i < prompts.size(); i++) {
            KeyPrompt keyPrompt = prompts.get(i);

            // 缓存翻译结果
            String key = getCachedKeyTranslation(keyPrompt.key);
            String desc = getCachedTranslation(keyPrompt.desc);
            if (isControlDown) {
                desc += "(" + keyPrompt.group + ":" + keyPrompt.desc + ")";
            }
            boolean pressed = Utils.isKeyPressedOfDesc(keyPrompt.desc);

            int y = baseY + (int) (16.0 * scale * i);
            int x;

            poseStack.pushPose();

            if (isCrosshair) {
                x = screenWidth / 2 - (int) (font.width(key + "==" + desc) * scale / 2);
                scaleHUD(poseStack, x, y, scale);
                KeyRenderer.drawKeyBoardKey(guiGraphics, x, y, key, pressed);
                KeyRenderer.drawText(guiGraphics, x + font.width(key) + 7, y + 2, desc);
            } else {
                if (position == 1 || position == 7 || position == 8) {
                    x = 5;
                    scaleHUD(poseStack, x, y, scale);
                    KeyRenderer.drawKeyBoardKey(guiGraphics, x, y, key, pressed);
                    KeyRenderer.drawText(guiGraphics, x + font.width(key) + 7, y + 2, desc);
                } else if (position == 3 || position == 4 || position == 5) {
                    x = screenWidth - 8;
                    scaleHUD(poseStack, x, y, scale);
                    KeyRenderer.drawText(guiGraphics, x - font.width(desc + key) - 3, y + 2, desc);
                    KeyRenderer.drawKeyBoardKey(guiGraphics, x - font.width(key), y, key, pressed);
                }
            }

            poseStack.popPose();
        }
    }

    private static String getCachedTranslation(String key) {
        return translationCache.computeIfAbsent(key, k -> Component.translatable(k).getString());
    }

    private static String getCachedKeyTranslation(String key) {
        return keyTranslationCache.computeIfAbsent(key, Utils::translateKey);
    }

    private static void scaleHUD(PoseStack poseStack, int x, int y, float scale) {
        poseStack.translate(x, y, 0);
        poseStack.scale(scale, scale, 1.0f);
        poseStack.translate(-x, -y, 0);
    }

    // 清理缓存
    public static void clearCache() {
        translationCache.clear();
        keyTranslationCache.clear();
        lastPromptListHash = 0;
    }
}