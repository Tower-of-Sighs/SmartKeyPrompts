package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.enchantment.Enchantments;

/** Reads the optional SlashBlade API after the loader confirms its presence. */
public final class SlashBladeIntegration {
    public static void tick() {
        var client = Minecraft.getInstance();
        var player = client.player;
        if (player == null || client.screen != null) return;
        var stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof ItemSlashBlade)) return;
        try {
            var state = stack.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
            var types = SwordType.from(stack);
            var art = state == null ? null : state.getSlashArtsKey();
            SlashBlade.tick(new SlashBlade.BladeState(
                    !types.contains(SwordType.ENCHANTED) || state != null && (state.isBroken() || state.isSealed()),
                    types.contains(SwordType.BEWITCHED), stack.getEnchantmentLevel(Enchantments.POWER_ARROWS),
                    state == null ? 0 : state.getProudSoulCount(),
                    art == null || SlashArtsRegistry.NONE.getId().equals(art) ? null : art.getPath()));
        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error("[SlashBlade] Cannot read held blade state", e);
        }
    }
}
