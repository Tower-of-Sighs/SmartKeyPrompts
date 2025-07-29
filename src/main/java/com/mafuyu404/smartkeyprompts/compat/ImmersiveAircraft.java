//package com.mafuyu404.smartkeyprompts.compat;
//
//import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
//import com.mafuyu404.smartkeyprompts.init.Utils;
//import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//import net.fabricmc.loader.api.FabricLoader;
//import net.minecraft.world.entity.player.Player;
//
//import java.util.Objects;
//
//public class ImmersiveAircraft {
//    private static final String modid = "immersive_aircraft_skp";
//    private static String cachedFallbackUseKey = null;
//
//    public static void init() {
//        ClientTickEvents.END_CLIENT_TICK.register(client -> {
//            if (!FabricLoader.getInstance().isModLoaded("immersive_aircraft")) return;
//            Player player = client.player;
//            if (player == null || client.screen != null) return;
//            String vehicle = Utils.getVehicleType();
//            if (vehicle != null && vehicle.startsWith("immersive_aircraft:")) {
//                SmartKeyPrompts.custom(modid, Utils.getKeyByDesc("key.inventory"), "immersive_aircraft.slot.upgrade");
//                SmartKeyPrompts.show(modid, "key.immersive_aircraft.dismount");
//                SmartKeyPrompts.show(modid, "key.immersive_aircraft.boost");
//
//                String keyUse = getFallbackUseKey();
//                SmartKeyPrompts.custom(modid, Objects.equals(keyUse, "key.keyboard.unknown") ? "key.mouse.right" : keyUse, "item.immersive_aircraft.item.weapon");
//
//                if (vehicle.equals("immersive_aircraft:biplane")) {
//                    SmartKeyPrompts.custom(modid, Utils.getKeyByDesc("key.jump"), "item.immersive_aircraft.engine");
//                }
//            }
//        });
//    }
//
//    private static String getFallbackUseKey() {
//
//        // 尝试获取fallback_use按键
//        String keyUse = Utils.getKeyByDesc("key.immersive_aircraft.fallback_use");
//        if (keyUse != null) {
//            cachedFallbackUseKey = keyUse;
//            return keyUse;
//        }
//
//        // 如果获取失败，尝试获取multi_use作为备选
//        keyUse = Utils.getKeyByDesc("key.immersive_aircraft.multi_use");
//        if (keyUse != null) {
//            cachedFallbackUseKey = keyUse;
//            return keyUse;
//        }
//
//        return "key.mouse.right";
//    }
//}