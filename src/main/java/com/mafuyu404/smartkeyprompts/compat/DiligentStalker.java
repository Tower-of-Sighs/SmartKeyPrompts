//package com.mafuyu404.smartkeyprompts.compat;
//
//import com.mafuyu404.smartkeyprompts.util.CommonUtils;
//import com.mafuyu404.smartkeyprompts.util.KeyUtils;
//import com.mafuyu404.smartkeyprompts.util.PlayerUtils;
//import com.mafuyu404.smartkeyprompts.util.PromptUtils;
//import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//import net.fabricmc.loader.api.FabricLoader;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.player.Player;
//
//public class DiligentStalker {
//    private static final String modid = "diligentstalker";
//
//    public static void init() {
//        ClientTickEvents.END_CLIENT_TICK.register(client -> {
//            if (!FabricLoader.getInstance().isModLoaded(modid)) return;
//            Player player = client.player;
//            if (player == null || client.screen != null) return;
//
//            String keyUse = KeyUtils.getKeyByDesc("key.use");
//            String keyShift = client.options.keyShift.getName();
//            String mainHandItem = PlayerUtils.getMainHandItemId();
//
//            if (mainHandItem.equals("diligentstalker:stalker_master")) {
//                PromptUtils.custom(modid, keyUse, "key.diligentstalker.remote_connect");
//            }
//
//            if (!(client.getCameraEntity() instanceof Player)) {
//                PromptUtils.show(modid, "key.diligentstalker.disconnect.desc");
//            }
//
//            Entity entity = PlayerUtils.getTargetedEntity();
//            if (entity == null) return;
//            boolean targetedDrone = CommonUtils.toPathString(entity.getType().toString()).equals("diligentstalker:drone_stalker");
//
//            if (mainHandItem.equals("diligentstalker:stalker_core")) {
//                PromptUtils.custom(modid, keyUse, "key.diligentstalker.connect");
//            }
//            if (targetedDrone) {
//                if (mainHandItem.equals("minecraft:sugar")) {
//                    PromptUtils.custom(modid, keyShift + "+" + keyUse, "key.diligentstalker.add_fuel");
//                } else if (mainHandItem.equals("diligentstalker:stalker_master")) {
//                    PromptUtils.custom(modid, keyShift + "+" + keyUse, "key.diligentstalker.record");
//                } else {
//                    PromptUtils.custom(modid, keyShift + "+" + keyUse, "key.diligentstalker.container");
//                }
//            }
//        });
//    }
//}