package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.init.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
public class DiligentStalker {
    private static final String modid = "diligentstalker";

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (!ModList.get().isLoaded(modid)) return;
        Player player = Minecraft.getInstance().player;
        if (player == null || Minecraft.getInstance().screen != null) return;

        String keyUse = Utils.getKeyByDesc("key.use");
        String keyShift = Minecraft.getInstance().options.keyShift.getName();
        String mainHandItem = Utils.getMainHandItemId(player);

        if (mainHandItem.equals("diligentstalker:stalker_master")) {
            SmartKeyPrompts.custom(modid, keyUse, "key.diligentstalker.remote_connect");
        }

        if (!(Minecraft.getInstance().getCameraEntity() instanceof Player)) {
            SmartKeyPrompts.show(modid, "key.diligentstalker.disconnect.desc");
        }

        Entity entity = Utils.getTargetedEntity(player);
//        System.out.print(entity+"\n");
        if (entity == null) return;
        boolean targetedDrone = Utils.toPathString(entity.getType().toString()).equals("diligentstalker:drone_stalker");

        if (mainHandItem.equals("diligentstalker:stalker_core")) {
            SmartKeyPrompts.custom(modid, keyUse, "key.diligentstalker.connect");
        }
        if (targetedDrone) {
            if (mainHandItem.equals("minecraft:sugar")) {
                SmartKeyPrompts.custom(modid, keyShift + keyUse, "key.diligentstalker.add_fuel");
            } else if (mainHandItem.equals("diligentstalker:stalker_master")) {
                SmartKeyPrompts.custom(modid, keyShift + keyUse, "key.diligentstalker.record");
            } else {
                SmartKeyPrompts.custom(modid, keyShift + keyUse, "key.diligentstalker.container");
            }
        }
    }
}
