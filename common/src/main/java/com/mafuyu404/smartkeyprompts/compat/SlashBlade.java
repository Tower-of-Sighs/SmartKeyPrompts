package com.mafuyu404.smartkeyprompts.compat;

import com.mafuyu404.smartkeyprompts.util.KeyUtils;
import com.mafuyu404.smartkeyprompts.util.PlayerUtils;
import com.mafuyu404.smartkeyprompts.util.PromptUtils;

/** Generates blade prompts from a loader-specific snapshot. */
public final class SlashBlade {
    private static final String modid = "slashblade_skp";

    /**
     * Describes the blade state needed to select prompts.
     *
     * @param unavailable whether advanced blade actions are disabled
     * @param bewitched whether summoned swords are supported
     * @param powerLevel the Power enchantment level
     * @param proudSoul the available soul count
     * @param slashArt the non-default slash art path, or {@code null}
     */
    public record BladeState(boolean unavailable, boolean bewitched, int powerLevel, int proudSoul, String slashArt) {}

    /**
     * Adds the prompts applicable to the held blade.
     *
     * @param state the snapshot, or {@code null} when no blade is held
     */
    public static void tick(BladeState state) {
        if (state == null) return;
        showBasicKeys();
        if (state.unavailable()) return;
        if (state.slashArt() != null && !"none".equals(state.slashArt())) showKeyForArt(state.slashArt());
        canUseSummonedSwordKeys(state);
    }

    private static void showBasicKeys() {
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

    private static void canUseSummonedSwordKeys(BladeState state) {
        if (!state.bewitched() || state.powerLevel() < 1) {
            return;
        }

        String keyShift = KeyUtils.getKeyByDesc("key.sneak");
        String keyForward = KeyUtils.getKeyByDesc("key.forward");
        String keyBack = KeyUtils.getKeyByDesc("key.back");
        String keyMiddle = KeyUtils.getKeyByDesc("key.slashblade.summon_blade");

        int proudSoul = state.proudSoul();

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

    private static void showKeyForArt(String artName) {
        String keyTranslationKey = "key.slashblade." + artName;

        PromptUtils.addDesc(keyTranslationKey)
                .forKey("key.mouse.right")
                .withKeyAlias("长按" + (KeyUtils.isRightClickKey("key.use") ? "右键" : KeyUtils.getKeyDisplayName("key.use")) + "后松开")
                .toGroup(modid);
    }
}