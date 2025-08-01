package com.mafuyu404.smartkeyprompts.init;

import com.mafuyu404.smartkeyprompts.util.KeyUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.mafuyu404.smartkeyprompts.SmartKeyPrompts.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class KeyStateManager {
    private static final Map<String, Boolean> keyStateCache = new ConcurrentHashMap<>();
    private static final Set<String> activeKeys = ConcurrentHashMap.newKeySet();
    private static final Map<String, Boolean> previousStates = new ConcurrentHashMap<>();

    private static int cleanupCounter = 0;
    private static final int CLEANUP_INTERVAL = 100;

    public static void registerKeys(Set<String> keyDescs) {
        activeKeys.addAll(keyDescs);
        for (String desc : keyDescs) {
            String key = KeyUtils.getKeyByDesc(desc);
            if (key != null && !key.isEmpty()) {
                activeKeys.add(key);
            }
        }
    }

    public static boolean isKeyPressed(String keyDesc) {
        return keyStateCache.getOrDefault(keyDesc, false);
    }

    public static void clearUnusedCache() {
        keyStateCache.keySet().retainAll(activeKeys);
        previousStates.keySet().retainAll(activeKeys);
    }

    public static void clearAllCache() {
        keyStateCache.clear();
        previousStates.clear();
        activeKeys.clear();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        updateKeyStates();

        cleanupCounter++;
        if (cleanupCounter >= CLEANUP_INTERVAL) {
            clearUnusedCache();
            cleanupCounter = 0;
        }
    }

    private static void updateKeyStates() {
        for (String keyDesc : activeKeys) {
            boolean currentState = checkKeyState(keyDesc);
            Boolean previousState = previousStates.get(keyDesc);

            // 只有状态改变时才更新缓存
            if (previousState == null || previousState != currentState) {
                keyStateCache.put(keyDesc, currentState);
                previousStates.put(keyDesc, currentState);
            }
        }
    }

    private static boolean checkKeyState(String keyDesc) {
        for (KeyMapping keyMapping : Minecraft.getInstance().options.keyMappings) {
            if (keyDesc.equals(keyMapping.getName())) {
                return keyMapping.isDown();
            }
        }
        return false;
    }
}