package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.init.Utils;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, value = Dist.CLIENT)
public class SlashBlade {
    private static final String modid = "slashblade_skp";

    // 缓存上一次的SA，避免重复显示
    private static String lastSlashArt = "";

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (!ModList.get().isLoaded("slashblade")) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        if (player == null || Minecraft.getInstance().screen != null) return;

        if (event.phase == TickEvent.Phase.START) {
            ItemStack mainHandItem = player.getMainHandItem();

            if (mainHandItem.getItem() instanceof ItemSlashBlade) {
                if (isBladeUnavailable(mainHandItem)) {
                    if (!lastSlashArt.isEmpty()) {
                        lastSlashArt = "";
                    }
                    return;
                }

                showBasicKeys(mainHandItem);

                showSlashArtKeys(mainHandItem);

                canUseSummonedSwordKeys(mainHandItem);

            } else {
                if (!lastSlashArt.isEmpty()) {
                    lastSlashArt = "";
                }
            }
        }
    }


    private static void showBasicKeys(ItemStack itemStack) {
        String keyShift = Utils.getKeyByDesc("key.sneak");
        String keyForward = Utils.getKeyByDesc("key.forward");
        String keyBack = Utils.getKeyByDesc("key.back");
        String keyUse = Utils.getKeyByDesc("key.use");
        String keyV = Utils.getKeyByDesc("key.slashblade.special_move");

        // 防御
        SmartKeyPrompts.addDesc("key.slashblade.guard")
                .forKey(keyShift)
                .withKeyAlias("按住" + Utils.getKeyDisplayName("key.sneak"))
                .toGroup(modid);

        // 瞬步/空中技巧
        if (Utils.hasTargetedEntityIsMob()) {
            SmartKeyPrompts.addDesc("key.slashblade.air_trick")
                    .forKey(keyShift + "+" + keyForward + "+" + keyV)
                    .withKeyAlias("按住" + Utils.getKeysDisplayName("key.sneak", "key.forward", "key.slashblade.special_move"))
                    .toGroup(modid);
        } else {
            SmartKeyPrompts.addDesc("key.slashblade.trick_up")
                    .forKey(keyShift + "+" + keyForward + "+" + keyV)
                    .withKeyAlias("按住" + Utils.getKeysDisplayName("key.sneak", "key.forward", "key.slashblade.special_move"))
                    .toGroup(modid);
        }

        // 闪避
        if (Utils.isPlayerMoving()) {
            SmartKeyPrompts.addDesc("key.slashblade.trick_dodge")
                    .forKey(keyV)
                    .toGroup(modid);
        }

        // 空中技能
        if (Utils.isPlayerInAir()) {
            SmartKeyPrompts.addDesc("key.slashblade.trick_down")
                    .forKey(keyShift + "+" + keyBack + "+" + keyV)
                    .withKeyAlias("按住" + Utils.getKeysDisplayName("key.sneak", "key.back", "key.slashblade.special_move"))
                    .toGroup(modid);

            SmartKeyPrompts.addDesc("key.slashblade.aerial_cleave")
                    .forKey(keyShift + "+" + keyBack + "+" + keyUse)
                    .withKeyAlias("按住" + Utils.getKeysDisplayName("key.sneak", "key.back", "key.use"))
                    .toGroup(modid);
        } else if (Utils.isPlayerOnGround()) {
            // 地面技能
            SmartKeyPrompts.addDesc("key.slashblade.upperslash")
                    .forKey(keyShift + "+" + keyBack + "+" + keyUse)
                    .withKeyAlias("按住" + Utils.getKeysDisplayName("key.sneak", "key.back", "key.use"))
                    .toGroup(modid);

            SmartKeyPrompts.addDesc("key.slashblade.rapid_slash")
                    .forKey(keyShift + "+" + keyForward + "+" + keyUse)
                    .withKeyAlias("按住" + Utils.getKeysDisplayName("key.sneak", "key.forward", "key.use"))
                    .toGroup(modid);

            // 跃升斩 - 按住Shift+S+长按右键
            SmartKeyPrompts.addDesc("key.slashblade.upperslash_jump")
                    .forKey(keyShift + "+" + keyBack + "+" + keyUse)
                    .withKeyAlias("按住" + Utils.getKeysDisplayName("key.sneak", "key.back") + "+长按" + 
                                 (Utils.isRightClickKey("key.use") ? "右键" : Utils.getKeyDisplayName("key.use")))
                    .toGroup(modid);
        }
    }
    private static void showSlashArtKeys(ItemStack itemStack) {
        ResourceLocation slashArtKey = getSlashArtKey(itemStack);

        if (slashArtKey != null && !isDefaultOrNoneSA(slashArtKey)) {
            String slashArtString = slashArtKey.toString();
            if (!slashArtString.equals(lastSlashArt)) {
                showSlashArtKey(slashArtKey);
                lastSlashArt = slashArtString;
            } else {
                showSlashArtKey(slashArtKey);
            }
        } else {
            lastSlashArt = "";
        }
    }

    private static void canUseSummonedSwordKeys(ItemStack itemStack) {
        if (!canUseSummonedSword(itemStack)) {
            return;
        }

        String keyShift = Utils.getKeyByDesc("key.sneak");
        String keyForward = Utils.getKeyByDesc("key.forward");
        String keyBack = Utils.getKeyByDesc("key.back");
        String keyMiddle = Utils.getKeyByDesc("key.slashblade.summon_blade");

        int proudSoul = getProudSoul(itemStack);

        if (proudSoul >= 2) {
            // 基础幻影剑
            SmartKeyPrompts.addDesc("key.slashblade.summonedswords")
                    .forKey(keyMiddle)
                    .toGroup(modid);

            if (proudSoul >= 20) {
                // 高级幻影剑技能
                SmartKeyPrompts.addDesc("key.slashblade.spiral_swords")
                        .forKey(keyMiddle)
                        .withKeyAlias("按住" + Utils.getKeyDisplayName("key.slashblade.summon_blade"))
                        .toGroup(modid);

                SmartKeyPrompts.addDesc("key.slashblade.storm_swords")
                        .forKey(keyShift + "+" + keyBack + "+" + keyMiddle)
                        .withKeyAlias("按住" + Utils.getKeysDisplayName("key.sneak", "key.back", "key.slashblade.summon_blade"))
                        .toGroup(modid);

                SmartKeyPrompts.addDesc("key.slashblade.blistering_swords")
                        .forKey(keyShift + "+" + keyForward + "+" + keyMiddle)
                        .withKeyAlias("按住" + Utils.getKeysDisplayName("key.sneak", "key.forward", "key.slashblade.summon_blade"))
                        .toGroup(modid);

                SmartKeyPrompts.addDesc("key.slashblade.heavy_rain_swords")
                        .forKey(keyShift + "+" + keyBack + "+" + keyForward + "+" + keyMiddle)
                        .withKeyAlias("按住" + Utils.getKeysDisplayName("key.sneak", "key.back", "key.forward", "key.slashblade.summon_blade"))
                        .toGroup(modid);
            }
        }
    }
    private static boolean canUseSummonedSword(ItemStack itemStack) {
        try {
            if (!SwordType.from(itemStack).contains(SwordType.BEWITCHED)) {
                return false;
            }

            int strengthLevel = itemStack.getEnchantmentLevel(Enchantments.POWER_ARROWS);
            if (strengthLevel < 1) {
                return false;
            }

            int proudSoul = getProudSoul(itemStack);
            return proudSoul >= 2;

        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error("[SlashBlade] Error checking phantom sword availability: {}", e.getMessage());
            return false;
        }
    }

    private static int getProudSoul(ItemStack itemStack) {
        try {
            return itemStack.getCapability(ItemSlashBlade.BLADESTATE)
                    .map(ISlashBladeState::getProudSoulCount)
                    .orElse(0);
        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error("[SlashBlade] Error getting proud soul: {}", e.getMessage());
            return 0;
        }
    }

    private static boolean isBladeUnavailable(ItemStack itemStack) {
        try {
            if (!SwordType.from(itemStack).contains(SwordType.ENCHANTED)) {
                return true;
            }

            return itemStack.getCapability(ItemSlashBlade.BLADESTATE)
                    .map(state -> state.isBroken() || state.isSealed())
                    .orElse(false);
        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error("[SlashBlade] Error checking blade state: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查是否为默认SA或none类型（不应该显示的SA）
     */
    private static boolean isDefaultOrNoneSA(ResourceLocation slashArtKey) {
        if (slashArtKey == null) return true;

        // 检查是否为none类型
        return SlashArtsRegistry.NONE.getId().equals(slashArtKey);
    }

    /**
     * 直接通过cap获取拔刀剑的SA信息
     */
    private static ResourceLocation getSlashArtKey(ItemStack itemStack) {
        try {
            return itemStack.getCapability(ItemSlashBlade.BLADESTATE)
                    .map(ISlashBladeState::getSlashArtsKey)
                    .orElse(null);
        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error("[SlashBlade] Error getting slash art key from capability: {}", e.getMessage());
            return null;
        }
    }

    private static void showSlashArtKey(ResourceLocation slashArtKey) {
        try {
            String artName = slashArtKey.getPath();

            // 过滤掉"none"类型的SA
            if ("none".equals(artName)) {
                return;
            }

            showKeyForArt(artName);

        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error("[SlashBlade] Error showing slash art key for '{}': {}", slashArtKey, e.getMessage());
        }
    }

    private static void showKeyForArt(String artName) {
        String keyTranslationKey = "key.slashblade." + artName;

        SmartKeyPrompts.addDesc(keyTranslationKey)
                .forKey("key.mouse.right")
                .withKeyAlias("长按" + (Utils.isRightClickKey("key.use") ? "右键" : Utils.getKeyDisplayName("key.use")) + "后松开")
                .toGroup(modid);
    }
}