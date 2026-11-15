package com.mafuyu404.smartkeyprompts.client;

import cc.sighs.oelib.event.fabric.DataReloadEvent;
import com.mafuyu404.smartkeyprompts.compat.*;
import com.mafuyu404.smartkeyprompts.data.KeyPromptEngine;
import com.mafuyu404.smartkeyprompts.env.JeiCompat;
import com.mafuyu404.smartkeyprompts.event.InputEvent;
import com.mafuyu404.smartkeyprompts.init.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

/** Connects Fabric client events to the shared prompt engine. */
public abstract class FabricClientEvents implements ClientModInitializer {
    /** Registers the version-specific tooltip callback. */
    protected abstract void registerTooltip();

    /** Reads optional blade state after its mod has been detected. */
    protected abstract void tickBlade();

    @Override
    public void onInitializeClient() {
        FabricKeybindings.registerKeyMapping();
        JeiCompat.init();
        DataReloadEvent.EVENT.register(KeyPromptEngine::onDataReload);
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            KeyGuideClient.tick();
            KeyPromptEngine.tick();
            HUD.tick();
            KeyStateManager.onClientTick();
            if (FabricLoader.getInstance().isModLoaded("slashblade")) tickBlade();
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            IceAndFire.tick();
            JEI.tick();
        });
        InputEvent.MOUSE_BUTTON_PRE.register((button, action, modifiers) -> ConfigAction.mouseAction(button, action));
        InputEvent.MOUSE_SCROLL.register((x, y, mx, my, left, middle, right) -> ConfigAction.wheelAction(y));
        HudRenderCallback.EVENT.register((graphics, tickDelta) -> HUD.onRenderGameOverlay(graphics));
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) ->
                ScreenEvents.afterRender(screen).register((s, graphics, x, y, tickDelta) -> HUD.onRenderScreenOverlay(graphics)));
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override public ResourceLocation getFabricId() {
                return java.util.Objects.requireNonNull(ResourceLocation.tryParse("smartkeyprompts:cache"));
            }
            @Override public void onResourceManagerReload(ResourceManager manager) { HUD.clearCache(); }
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> HUD.clearCache());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            KeyGuideClient.clear();
            HUD.clearCache();
        });
        registerTooltip();
    }
}
