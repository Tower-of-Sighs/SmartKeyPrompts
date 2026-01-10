package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import com.mafuyu404.smartkeyprompts.util.EnchantmentUtils;
import com.mafuyu404.smartkeyprompts.util.KeyUtils;
import com.mafuyu404.smartkeyprompts.util.PlayerUtils;
import com.mafuyu404.smartkeyprompts.util.PromptUtils;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;

public class SlashBlade {
    private static final String modid = "slashblade_skp";

    // 缓存上一次的SA，避免重复显示
    private static String lastSlashArt = "";

    public static void init() {
        ClientTickEvents.START_CLIENT_TICK.register(SlashBlade::tick);
    }

    public static void tick(Minecraft client) {
        if (!FabricLoader.getInstance().isModLoaded("slashblade")) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        if (player == null || Minecraft.getInstance().screen != null) return;

        ItemStack mainHandItem = player.getMainHandItem();

        if (mainHandItem.getItem() instanceof ItemSlashBlade) {
            showBasicKeys(mainHandItem);
            if (isBladeUnavailable(mainHandItem)) {
                if (!lastSlashArt.isEmpty()) {
                    lastSlashArt = "";
                }
                return;
            }


            showSlashArtKeys(mainHandItem);

            var registryAccess = player.registryAccess();

            canUseSummonedSwordKeys(mainHandItem, registryAccess);

        } else {
            if (!lastSlashArt.isEmpty()) {
                lastSlashArt = "";
            }
        }
    }


    private static void showBasicKeys(ItemStack itemStack) {
        String keyShift = KeyUtils.getKeyByDesc("key.sneak");
        String keyForward = KeyUtils.getKeyByDesc("key.forward");
        String keyBack = KeyUtils.getKeyByDesc("key.back");
        String keyUse = KeyUtils.getKeyByDesc("key.use");
        String keyV = KeyUtils.getKeyByDesc("key.slashblade.special_move");

        // 防御
        PromptUtils.addDesc("key.slashblade.guard")
                .forKey(keyShift)
                .withKeyAlias("按住" + KeyUtils.getKeyDisplayName("key.sneak"))
                .toGroup(modid);

        // 瞬步/空中技巧
        if (PlayerUtils.hasTargetedEntityIsMob()) {
            PromptUtils.addDesc("key.slashblade.air_trick")
                    .forKey(keyShift + "+" + keyForward + "+" + keyV)
                    .withKeyAlias("按住" + KeyUtils.getKeysDisplayName("key.sneak", "key.forward", "key.slashblade.special_move"))
                    .toGroup(modid);
        } else {
            PromptUtils.addDesc("key.slashblade.trick_up")
                    .forKey(keyShift + "+" + keyForward + "+" + keyV)
                    .withKeyAlias("按住" + KeyUtils.getKeysDisplayName("key.sneak", "key.forward", "key.slashblade.special_move"))
                    .toGroup(modid);
        }

        // 闪避
        if (PlayerUtils.isPlayerMoving()) {
            PromptUtils.addDesc("key.slashblade.trick_dodge")
                    .forKey(keyV)
                    .toGroup(modid);
        }

        // 空中技能
        if (PlayerUtils.isPlayerInAir()) {
            PromptUtils.addDesc("key.slashblade.trick_down")
                    .forKey(keyShift + "+" + keyBack + "+" + keyV)
                    .withKeyAlias("按住" + KeyUtils.getKeysDisplayName("key.sneak", "key.back", "key.slashblade.special_move"))
                    .toGroup(modid);

            PromptUtils.addDesc("key.slashblade.aerial_cleave")
                    .forKey(keyShift + "+" + keyBack + "+" + keyUse)
                    .withKeyAlias("按住" + KeyUtils.getKeysDisplayName("key.sneak", "key.back", "key.use"))
                    .toGroup(modid);
        } else if (PlayerUtils.isPlayerOnGround()) {
            // 地面技能
            PromptUtils.addDesc("key.slashblade.upperslash")
                    .forKey(keyShift + "+" + keyBack + "+" + keyUse)
                    .withKeyAlias("按住" + KeyUtils.getKeysDisplayName("key.sneak", "key.back", "key.use"))
                    .toGroup(modid);

            PromptUtils.addDesc("key.slashblade.rapid_slash")
                    .forKey(keyShift + "+" + keyForward + "+" + keyUse)
                    .withKeyAlias("按住" + KeyUtils.getKeysDisplayName("key.sneak", "key.forward", "key.use"))
                    .toGroup(modid);

            // 跃升斩 - 按住Shift+S+长按右键
            PromptUtils.addDesc("key.slashblade.upperslash_jump")
                    .forKey(keyShift + "+" + keyBack + "+" + keyUse)
                    .withKeyAlias("按住" + KeyUtils.getKeysDisplayName("key.sneak", "key.back") + "+长按" +
                            (KeyUtils.isRightClickKey("key.use") ? "右键" : KeyUtils.getKeyDisplayName("key.use")))
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


    private static void canUseSummonedSwordKeys(ItemStack itemStack, RegistryAccess registryAccess) {
        if (!canUseSummonedSword(itemStack, registryAccess)) {
            return;
        }

        String keyShift = KeyUtils.getKeyByDesc("key.sneak");
        String keyForward = KeyUtils.getKeyByDesc("key.forward");
        String keyBack = KeyUtils.getKeyByDesc("key.back");
        String keyMiddle = KeyUtils.getKeyByDesc("key.slashblade.summon_blade");

        int proudSoul = getProudSoul(itemStack);

        if (proudSoul >= 2) {
            // 基础幻影剑
            PromptUtils.addDesc("key.slashblade.summonedswords")
                    .forKey(keyMiddle)
                    .toGroup(modid);

            if (proudSoul >= 20) {
                // 高级幻影剑技能
                PromptUtils.addDesc("key.slashblade.spiral_swords")
                        .forKey(keyMiddle)
                        .withKeyAlias("按住" + KeyUtils.getKeyDisplayName("key.slashblade.summon_blade"))
                        .toGroup(modid);

                PromptUtils.addDesc("key.slashblade.storm_swords")
                        .forKey(keyShift + "+" + keyBack + "+" + keyMiddle)
                        .withKeyAlias("按住" + KeyUtils.getKeysDisplayName("key.sneak", "key.back", "key.slashblade.summon_blade"))
                        .toGroup(modid);

                PromptUtils.addDesc("key.slashblade.blistering_swords")
                        .forKey(keyShift + "+" + keyForward + "+" + keyMiddle)
                        .withKeyAlias("按住" + KeyUtils.getKeysDisplayName("key.sneak", "key.forward", "key.slashblade.summon_blade"))
                        .toGroup(modid);

                PromptUtils.addDesc("key.slashblade.heavy_rain_swords")
                        .forKey(keyShift + "+" + keyBack + "+" + keyForward + "+" + keyMiddle)
                        .withKeyAlias("按住" + KeyUtils.getKeysDisplayName("key.sneak", "key.back", "key.forward", "key.slashblade.summon_blade"))
                        .toGroup(modid);
            }
        }
    }

    private static boolean canUseSummonedSword(ItemStack itemStack, RegistryAccess registryAccess) {
        try {
            if (!SwordType.from(itemStack).contains(SwordType.BEWITCHED)) {
                return false;
            }

            var holder = registryAccess.registryOrThrow(Registries.ENCHANTMENT)
                    .getHolder(Enchantments.POWER)
                    .orElse(null);

            int strengthLevel = EnchantmentUtils.getTagEnchantmentLevel(holder, itemStack);
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
            return CapabilitySlashBlade.getBladeState(itemStack)
                    .map(ISlashBladeState::getProudSoulCount)
                    .orElse(0);
        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error(
                    "[SlashBlade] Error getting proud soul", e
            );
            return 0;
        }
    }

    private static boolean isBladeUnavailable(ItemStack itemStack) {
        try {
            if (!SwordType.from(itemStack).contains(SwordType.ENCHANTED)) {
                return true;
            }

            return CapabilitySlashBlade.getBladeState(itemStack)
                    .map(state -> state.isBroken() || state.isSealed())
                    .orElse(false);

        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error(
                    "[SlashBlade] Error checking blade state", e
            );
            return false;
        }
    }


    /**
     * 检查是否为默认SA或none类型（不应该显示的SA）
     */
    private static boolean isDefaultOrNoneSA(ResourceLocation slashArtKey) {
        if (slashArtKey == null) return true;

        // 检查是否为none类型
        return SlashArts.getRegistryKey(SlashArtsRegistry.NONE).equals(slashArtKey);
    }

    /**
     * 直接通过cap获取拔刀剑的SA信息
     */
    private static @Nullable ResourceLocation getSlashArtKey(ItemStack itemStack) {
        try {
            return CapabilitySlashBlade.getBladeState(itemStack)
                    .map(ISlashBladeState::getSlashArtsKey)
                    .orElse(null);
        } catch (Exception e) {
            SmartKeyPrompts.LOGGER.error(
                    "[SlashBlade] Error getting slash art key", e
            );
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

        PromptUtils.addDesc(keyTranslationKey)
                .forKey("key.mouse.right")
                .withKeyAlias("长按" + (KeyUtils.isRightClickKey("key.use") ? "右键" : KeyUtils.getKeyDisplayName("key.use")) + "后松开")
                .toGroup(modid);
    }
}