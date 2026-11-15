package com.mafuyu404.smartkeyprompts.client;

import cc.sighs.oelib.event.neoforge.DataReloadEvent;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.compat.EMI;
import com.mafuyu404.smartkeyprompts.compat.IceAndFire;
import com.mafuyu404.smartkeyprompts.compat.JEI;
import com.mafuyu404.smartkeyprompts.data.KeyPromptEngine;
import com.mafuyu404.smartkeyprompts.init.ConfigAction;
import com.mafuyu404.smartkeyprompts.init.HUD;
import com.mafuyu404.smartkeyprompts.init.KeyStateManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/** Adapts NeoForge client events to shared prompt operations. */
@EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
public final class NeoForgeClientEvents {
    @SubscribeEvent
    public static void tick(ClientTickEvent.Pre event) {
        KeyGuideClient.tick();
        KeyPromptEngine.tick();
        IceAndFire.tick();
        JEI.tick();
        HUD.tick();
        KeyStateManager.onClientTick();
    }

    @SubscribeEvent
    public static void dataReload(DataReloadEvent event) {
        KeyPromptEngine.onDataReload(event.getDataClass(), event.getLoadedCount(), event.getInvalidCount());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void renderHud(RenderGuiEvent.Post event) {
        HUD.onRenderGameOverlay(event.getGuiGraphics());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void renderScreen(ScreenEvent.Render.Post event) {
        HUD.onRenderScreenOverlay(event.getGuiGraphics());
    }

    @SubscribeEvent
    public static void mouseButton(InputEvent.MouseButton.Pre event) {
        if (ConfigAction.mouseAction(event.getButton(), event.getAction())) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void mouseScroll(InputEvent.MouseScrollingEvent event) {
        if (ConfigAction.wheelAction(event.getScrollDeltaY())) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void tooltip(ItemTooltipEvent event) {
        JEI.itemTooltip();
        EMI.itemTooltip();
    }

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) manager -> HUD.clearCache());
    }

    @SubscribeEvent
    public static void login(ClientPlayerNetworkEvent.LoggingIn event) {
        HUD.clearCache();
    }

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut event) {
        KeyGuideClient.clear();
        HUD.clearCache();
    }
}
