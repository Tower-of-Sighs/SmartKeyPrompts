package com.mafuyu404.smartkeyprompts.init;

import com.mafuyu404.smartkeyprompts.Config;
import com.mafuyu404.smartkeyprompts.util.NBTUtils;
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
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.mafuyu404.smartkeyprompts.SmartKeyPrompts.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class HUD {
    public static final List<KeyPrompt> KeyPromptList = Collections.synchronizedList(new ArrayList<>());
    public static final Set<KeyPrompt> KeyPromptCache = new CopyOnWriteArraySet<>();
    private static volatile Font font;
    public static volatile KeyMapping[] KeyMappingCache;

    private static final Map<String, String> translationCache = new ConcurrentHashMap<>();
    private static final Map<String, String> keyTranslationCache = new ConcurrentHashMap<>();

    private static volatile List<KeyPrompt> cachedDefaultPrompts = Collections.emptyList();
    private static volatile List<KeyPrompt> cachedCrosshairPrompts = Collections.emptyList();
    private static volatile int lastPromptListHash = 0;

    public static void addCache(KeyPrompt keyPrompt) {
        if (keyPrompt == null) return;
        KeyPromptCache.add(keyPrompt);
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        if (event.phase == TickEvent.Phase.START) {
            if (!Utils.isKeyPressed(ModKeybindings.CONTROL_KEY.getKey().getValue())) {
                List<? extends String> blacklist = Config.BLACKLIST.get();

                List<KeyPrompt> toAdd = new ArrayList<>();
                for (KeyPrompt keyPrompt : KeyPromptCache) {
                    if (keyPrompt != null && !blacklist.contains(keyPrompt.group)) {
                        toAdd.add(keyPrompt);
                    }
                }

                synchronized (KeyPromptList) {
                    KeyPromptList.clear();
                    KeyPromptList.addAll(toAdd);
                }
            } else {
                KeyPrompt[] controlPrompts = {
                        new KeyPrompt(MODID, "key.mouse.left", "key.smartkeyprompts.keybinding", true),
                        new KeyPrompt(MODID, "key.mouse.wheel", "key.smartkeyprompts.scale", true),
                        new KeyPrompt(MODID, "key.mouse.right", "key.smartkeyprompts.position", true)
                };

                synchronized (KeyPromptList) {
                    for (KeyPrompt keyPrompt : controlPrompts) {
                        if (!containsKeyPrompt(keyPrompt)) {
                            KeyPromptList.add(keyPrompt);
                        }
                    }
                }
            }

            KeyPromptCache.clear();

            updateCachedPrompts();
            registerActiveKeys();
        }

        if (!(minecraft.screen instanceof KeyBindsScreen) && KeyMappingCache != null) {
            minecraft.options.keyMappings = KeyMappingCache;
            KeyMappingCache = null;
        }
    }

    private static boolean containsKeyPrompt(KeyPrompt target) {
        for (KeyPrompt prompt : HUD.KeyPromptList) {
            if (prompt != null && prompt.equals(target)) {
                return true;
            }
        }
        return false;
    }

    private static void registerActiveKeys() {
        Set<String> activeKeyDescs = new HashSet<>();
        synchronized (KeyPromptList) {
            for (KeyPrompt keyPrompt : KeyPromptList) {
                if (keyPrompt != null) {
                    if (keyPrompt.desc != null) {
                        activeKeyDescs.add(keyPrompt.desc);
                    }
                    if (keyPrompt.key != null) {
                        activeKeyDescs.add(keyPrompt.key);
                    }
                }
            }
        }
        KeyStateManager.registerKeys(activeKeyDescs);
    }

    private static void updateCachedPrompts() {
        List<KeyPrompt> currentList;
        synchronized (KeyPromptList) {
            currentList = new ArrayList<>(KeyPromptList);
        }

        int currentHash = currentList.hashCode();
        if (currentHash != lastPromptListHash) {
            List<KeyPrompt> defaultPrompts = new ArrayList<>();
            List<KeyPrompt> crosshairPrompts = new ArrayList<>();

            for (KeyPrompt keyPrompt : currentList) {
                if (keyPrompt != null) {
                    if ("default".equals(keyPrompt.position)) {
                        defaultPrompts.add(keyPrompt);
                    } else if ("crosshair".equals(keyPrompt.position)) {
                        crosshairPrompts.add(keyPrompt);
                    }
                }
            }

            cachedDefaultPrompts = List.copyOf(defaultPrompts);
            cachedCrosshairPrompts = List.copyOf(crosshairPrompts);
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

    @SubscribeEvent
    public static void onResourceReload(AddReloadListenerEvent event) {
        clearCache();
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        clearCache();
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        clearCache();
    }


    private static void drawHud(GuiGraphics guiGraphics) {
        List<KeyPrompt> defaultPrompts = cachedDefaultPrompts;
        List<KeyPrompt> crosshairPrompts = cachedCrosshairPrompts;

        if (defaultPrompts.isEmpty() && crosshairPrompts.isEmpty()) return;

        Window window = Minecraft.getInstance().getWindow();
        int screenWidth = window.getGuiScaledWidth();
        int screenHeight = window.getGuiScaledHeight();

        if (font == null) font = Minecraft.getInstance().font;

        float scale = Config.SCALE.get().floatValue();
        int position = Config.POSITION.get();

        if (position == 2 || position == 6) {
            return;
        }

        int y0 = calculateY0(position, screenHeight, defaultPrompts.size());
        boolean isControlDown = ModKeybindings.CONTROL_KEY.isDown();

        PoseStack poseStack = guiGraphics.pose();

        renderKeyPrompts(guiGraphics, poseStack, defaultPrompts, position, screenWidth, y0, scale, isControlDown, false);
        renderKeyPrompts(guiGraphics, poseStack, crosshairPrompts, position, screenWidth, screenHeight / 2 + 7, scale, isControlDown, true);
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

        // 找出当前按下的最复杂的按键组合
        String mostComplexPressedKey = findMostComplexPressedKey(prompts);

        for (int i = 0; i < prompts.size(); i++) {
            KeyPrompt keyPrompt = prompts.get(i);
            if (keyPrompt == null) continue;

            // 优先使用按键别名，如果没有则使用翻译后的按键
            String key = keyPrompt.keyAlias != null ? keyPrompt.keyAlias : getCachedKeyTranslation(keyPrompt.key);
            String desc = getCachedTranslation(keyPrompt.desc);
            if (isControlDown) {
                desc += "(" + keyPrompt.group + ":" + keyPrompt.desc + ")";
            }

            boolean pressed;
            // 只有最复杂的匹配按键才显示为按下
            pressed = shouldShowAsPressed(keyPrompt.key, mostComplexPressedKey);

            if (!pressed) {
                pressed = Utils.isKeyPressedOfDesc(keyPrompt.desc);
            }

            int y = baseY + (int) (16.0 * scale * i);
            int x;

            poseStack.pushPose();

            if (isCrosshair) {
                x = screenWidth / 2 - (int) (font.width(key + "==" + desc) * scale / 2);
                scaleHUD(poseStack, x, y, scale);
                KeyRenderer.drawKeyBoardKey(guiGraphics, x, y, key, pressed);
                KeyRenderer.drawText(guiGraphics, x + font.width(key) + 12, y + 3, desc);
            } else {
                if (position == 1 || position == 7 || position == 8) {
                    x = 5;
                    scaleHUD(poseStack, x, y, scale);
                    KeyRenderer.drawKeyBoardKey(guiGraphics, x, y, key, pressed);
                    KeyRenderer.drawText(guiGraphics, x + font.width(key) + 12, y + 3, desc);
                } else if (position == 3 || position == 4 || position == 5) {
                    x = screenWidth - 8;
                    scaleHUD(poseStack, x, y, scale);
                    KeyRenderer.drawText(guiGraphics, x - font.width(desc + key) + 2, y + 3, desc);
                    KeyRenderer.drawKeyBoardKey(guiGraphics, x - font.width(key), y, key, pressed);
                }
            }

            poseStack.popPose();
        }
    }

    /**
     * 找出当前按下的最复杂的按键组合
     */
    private static String findMostComplexPressedKey(List<KeyPrompt> prompts) {
        String mostComplexKey = null;
        int maxComplexity = 0;

        for (KeyPrompt keyPrompt : prompts) {
            if (keyPrompt == null || keyPrompt.key == null || keyPrompt.isCustom) continue;

            if (Utils.isPhysicalKeyPressed(keyPrompt.key)) {
                int complexity = getKeyComplexity(keyPrompt.key);
                if (complexity > maxComplexity) {
                    maxComplexity = complexity;
                    mostComplexKey = keyPrompt.key;
                }
            }
        }

        return mostComplexKey;
    }

    private static boolean shouldShowAsPressed(String keyName, String mostComplexPressedKey) {
        if (keyName == null) return false;

        // 如果没有找到最复杂的按键，使用原来的检测方式
        if (mostComplexPressedKey == null) {
            return Utils.isPhysicalKeyPressed(keyName);
        }

        // 只有当前按键就是最复杂的按键时才显示为按下
        if (keyName.equals(mostComplexPressedKey)) {
            return true;
        }

        // 如果当前按键被包含在最复杂的按键中，则不显示为按下
        if (isKeyContainedIn(keyName, mostComplexPressedKey)) {
            return false;
        }

        // 其他情况使用原来的检测方式
        return Utils.isPhysicalKeyPressed(keyName);
    }

    /**
     * 计算按键组合的复杂度（按键数量）
     */
    private static int getKeyComplexity(String keyName) {
        if (keyName == null) return 0;
        return keyName.contains("+") ? keyName.split("\\+").length : 1;
    }

    /**
     * 检查一个按键是否被另一个按键组合包含
     */
    private static boolean isKeyContainedIn(String simpleKey, String complexKey) {
        if (simpleKey == null || complexKey == null) return false;
        if (!complexKey.contains("+")) return false;

        String[] complexKeys = complexKey.split("\\+");
        String[] simpleKeys = simpleKey.split("\\+");

        // 检查 simpleKey 的所有按键是否都在 complexKey 中
        for (String simple : simpleKeys) {
            boolean found = false;
            for (String complex : complexKeys) {
                if (simple.trim().equals(complex.trim())) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }

        return simpleKeys.length < complexKeys.length;
    }


    private static String getCachedTranslation(String key) {
        if (key == null) return "";
        return translationCache.computeIfAbsent(key, k -> Component.translatable(k).getString());
    }

    private static String getCachedKeyTranslation(String key) {
        if (key == null) return "";
        return keyTranslationCache.computeIfAbsent(key, Utils::translateKey);
    }

    private static void scaleHUD(PoseStack poseStack, int x, int y, float scale) {
        poseStack.translate(x, y, 0);
        poseStack.scale(scale, scale, 1.0f);
        poseStack.translate(-x, -y, 0);
    }

    public static void clearCache() {
        NBTUtils.clearCache();
        translationCache.clear();
        keyTranslationCache.clear();
        lastPromptListHash = 0;
        KeyStateManager.clearAllCache();
        cachedDefaultPrompts = Collections.emptyList();
        cachedCrosshairPrompts = Collections.emptyList();
    }
}