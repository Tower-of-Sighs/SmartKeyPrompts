package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.env.JeiCompat;
import com.mafuyu404.smartkeyprompts.util.CommonUtils;
import com.mafuyu404.smartkeyprompts.util.PromptUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class JEI {
    private static final String modid = "jei_skp";

    public static void init() {
        ItemTooltipCallback.EVENT.register(JEI::itemTooltip);
        ClientTickEvents.END_CLIENT_TICK.register(JEI::tick);
    }

    public static void itemTooltip(ItemStack stack, Item.TooltipContext tooltipContext, TooltipFlag tooltipType, List<Component> lines) {
        if (FabricLoader.getInstance().isModLoaded("jei")) {
            if (CommonUtils.isScreenOpen()) {
                PromptUtils.show(modid, "key.jei.showRecipe");
                PromptUtils.show(modid, "key.jei.showUses");
                PromptUtils.show(modid, "key.jei.bookmark");
            }
        }
    }

    public static void tick(Minecraft client) {
        if (CommonUtils.isScreenOpen()) {
//        SmartKeyPrompts.addDesc("key.jei.showRecipe").atPosition("crosshair").toGroup("jei_skp");
//        SmartKeyPrompts.addDesc("key.jei.bookmark").withCustom(true).forKey("key.mouse.4").atPosition("crosshair").toGroup("jei_skp");
            if (!FabricLoader.getInstance().isModLoaded("jei")) return;
            if (JeiCompat.isEnabled()) {
                PromptUtils.show(modid, "key.jei.recipeBack");
                PromptUtils.show(modid, "key.jei.toggleOverlay");
            }
        }
    }
}
