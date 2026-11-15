package com.mafuyu404.smartkeyprompts.client;

import cc.sighs.oelib.event.forge.DataReloadEvent;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.compat.EMI;
import com.mafuyu404.smartkeyprompts.compat.IceAndFire;
import com.mafuyu404.smartkeyprompts.compat.JEI;
import com.mafuyu404.smartkeyprompts.data.KeyPromptEngine;
import com.mafuyu404.smartkeyprompts.init.ConfigAction;
import com.mafuyu404.smartkeyprompts.init.HUD;
import com.mafuyu404.smartkeyprompts.init.KeyStateManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

/** Adapts NeoForge client events to shared prompt operations. */
@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
public final class ForgeClientEvents {
    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (net.minecraftforge.fml.ModList.get().isLoaded("slashblade")) com.mafuyu404.smartkeyprompts.compat.SlashBladeIntegration.tick();
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
        if (ConfigAction.wheelAction(event.getScrollDelta())) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void tooltip(ItemTooltipEvent event) {
        JEI.itemTooltip();
        EMI.itemTooltip();
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
