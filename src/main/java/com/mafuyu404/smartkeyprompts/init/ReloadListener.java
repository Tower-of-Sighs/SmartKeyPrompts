package com.mafuyu404.smartkeyprompts.init;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.data.KeyPromptEngine;
import com.mafuyu404.smartkeyprompts.network.NetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ReloadListener {

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        // 注册网络处理器
        event.enqueueWork(NetworkHandler::register);
    }

    /**
     * 当接收到服务端同步的数据包时，强制重载客户端缓存
     */
    public static void onDataSyncReceived() {
        SmartKeyPrompts.LOGGER.info("Received datapack sync from server, reloading client cache...");
        KeyPromptEngine.forceReload();
    }
}