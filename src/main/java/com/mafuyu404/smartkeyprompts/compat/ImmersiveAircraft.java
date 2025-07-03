package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.init.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
public class ImmersiveAircraft {
    private static final String modid = "immersive_aircraft_skp";
    
    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (!ModList.get().isLoaded("immersive_aircraft")) return;
        Player player = Minecraft.getInstance().player;
        if (player == null || Minecraft.getInstance().screen != null) return;
        String vehicle = Utils.getVehicleType(player);
        if (vehicle != null && vehicle.startsWith("immersive_aircraft:")) {
            SmartKeyPrompts.custom(modid, Utils.getKeyByDesc("key.inventory"), "immersive_aircraft.slot.upgrade");
            SmartKeyPrompts.show(modid, "key.immersive_aircraft.dismount");
            SmartKeyPrompts.show(modid, "key.immersive_aircraft.boost");
            String keyUse = Utils.getKeyByDesc("key.immersive_aircraft.fallback_use");
            SmartKeyPrompts.custom(modid, Objects.equals(keyUse, "key.keyboard.unknown") ? "key.mouse.right" : keyUse, "item.immersive_aircraft.item.weapon");
            if (vehicle.equals("immersive_aircraft:biplane")) {
                SmartKeyPrompts.custom(modid, Utils.getKeyByDesc("key.jump"), "item.immersive_aircraft.engine");
            }
        }
    }
}
