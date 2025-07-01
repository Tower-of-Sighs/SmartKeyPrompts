package com.mafuyu404.smartkeyprompts.init;

import com.mafuyu404.smartkeyprompts.Config;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
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

    @SubscribeEvent
    public static void action(InputEvent.Key event) {
        if (event.getAction() != InputConstants.PRESS) return;
        if (event.getKey() == KeyBindings.SWITCH_POSITION_KEY.getKey().getValue()) {
            Options options = Minecraft.getInstance().options;
            if (options.keyShift.isDown() || Screen.hasShiftDown()) {
                List<String> currentKey = bindingInfoList.stream()
                        .filter(keyBindingInfo -> !keyBindingInfo.custom && Utils.getKeyByDesc(keyBindingInfo.desc) != null)
                        .map(keyBindingInfo -> keyBindingInfo.desc).toList();
                modifyKey(currentKey);
                return;
            }
            if (Config.POSITION.get() == 8) Config.POSITION.set(1);
            else Config.POSITION.set(Config.POSITION.get() + 1);
            Config.POSITION.save();
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;
        if (event.phase == TickEvent.Phase.START) {
            bindingInfoList.clear();
            bindingInfoList.addAll(bindingInfoCache);
            bindingInfoCache.clear();
        }
        if (!(minecraft.screen instanceof KeyBindsScreen) && KeyMappingCache != null) {
            minecraft.options.keyMappings = KeyMappingCache;
            KeyMappingCache = null;
        }
    }
//    @SubscribeEvent
//    public static void test(TickEvent.ClientTickEvent event) {
//        if (Minecraft.getInstance().player == null) return;
//        if (event.phase == TickEvent.Phase.END) {
//            SmartKeyPrompts.show("default", "key.jade.narratet");
//            SmartKeyPrompts.show("default", "key.sprint");
////            SmartKeyPrompts.show("default", "key.jei.cheatOneItem");
//            if (Minecraft.getInstance().player.isSprinting()) {
//                SmartKeyPrompts.custom("default", "key.keyboard.left.shift+key.keyboard.space", "key.jump");
//            }
//        }
//    }
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

        for (KeyBindingInfo keyBindingInfo : bindingInfoList) {
            poseStack.pushPose();

            String key = translateKey(keyBindingInfo.key);
            String desc = Component.translatable(keyBindingInfo.desc).getString();

            if (position == 1 || position == 7 || position == 8) {
                x = 5;
                poseStack.translate(x, y, 0);
                poseStack.scale(scale, scale, 1.0f);
                poseStack.translate(-x, -y, 0);
                KeyRenderer.drawKeyBoardKey(guiGraphics, x, y, key);
                KeyRenderer.drawText(guiGraphics, x + font.width(key) + 7, y + 2, desc);
            }
            if (position == 3 || position == 4 || position == 5) {
                x = screenWidth - 8;
                poseStack.translate(x, y, 0);
                poseStack.scale(scale, scale, 1.0f);
                poseStack.translate(-x, -y, 0);
                KeyRenderer.drawText(guiGraphics, x - font.width(desc + key) - 3, y + 2, desc);
                KeyRenderer.drawKeyBoardKey(guiGraphics, x - font.width(key), y, key);
            }
            y += 14;

            poseStack.popPose();
        }
    }

    private static String translateKey(String key) {
        if (key.contains("+")) {
            StringBuilder result = new StringBuilder();
            List.of(key.split("\\+")).forEach(part -> {
                if (!result.isEmpty()) result.append("+");
                result.append(translateKey(part));
            });
            return result.toString();
        }
        String text = Component.translatable(key).getString();
        if (text.contains("key.keyboard")) {
            text = text.split("\\.")[2].toUpperCase();
        }
        return text;
    }

    public static ArrayList<KeyBindingInfo> getAllKeyBindings() {
        Minecraft mc = Minecraft.getInstance();
        ArrayList<KeyBindingInfo> bindingList = new ArrayList<>();
        for (KeyMapping binding : mc.options.keyMappings) {
            KeyBindingInfo info = new KeyBindingInfo(
                    "",
                    binding.getKey().getName(),
                    binding.getName(),
                    false
            );
            bindingList.add(info);
        }
        return bindingList;
    }

    // 按键绑定信息类
    public record KeyBindingInfo(String id, String key, String desc, boolean custom) {}

    public static void modifyKey(List<String> keyNames) {
        if (KeyMappingCache != null) return;

        Minecraft minecraft = Minecraft.getInstance();

        KeyMappingCache = minecraft.options.keyMappings;

        KeyMapping[] allKeys = minecraft.options.keyMappings;
        Set<String> targetKeys = keyNames.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        KeyMapping[] filteredKeys = new KeyMapping[allKeys.length];
        int count = 0;

        for (KeyMapping key : allKeys) {
            String name = key.getName().toLowerCase();
            if (targetKeys.contains(name)) {
                filteredKeys[count++] = key;
            }
        }

        KeyMapping[] tempKeys = new KeyMapping[count];
        System.arraycopy(filteredKeys, 0, tempKeys, 0, count);

        minecraft.options.keyMappings = tempKeys;
        minecraft.setScreen(new KeyBindsScreen(null, minecraft.options));

    }
}
