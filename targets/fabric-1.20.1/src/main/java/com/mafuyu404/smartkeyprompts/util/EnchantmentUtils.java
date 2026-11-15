package com.mafuyu404.smartkeyprompts.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EnchantmentUtils {

    public static int getTagEnchantmentLevel(Enchantment enchantment, ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        } else {
            ResourceLocation resourcelocation = EnchantmentHelper.getEnchantmentId(enchantment);
            ListTag listtag = stack.getEnchantmentTags();

            for (int i = 0; i < listtag.size(); ++i) {
                CompoundTag compoundtag = listtag.getCompound(i);
                ResourceLocation resourcelocation1 = EnchantmentHelper.getEnchantmentId(compoundtag);
                if (resourcelocation1 != null && resourcelocation1.equals(resourcelocation)) {
                    return EnchantmentHelper.getEnchantmentLevel(compoundtag);
                }
            }

            return 0;
        }
    }
}
