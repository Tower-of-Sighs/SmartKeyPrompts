package com.mafuyu404.smartkeyprompts;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.DoubleValue SCALE = BUILDER.comment("缩放").defineInRange("scale", 0.8, 0, 10);

    public static final ForgeConfigSpec.IntValue POSITION = BUILDER.comment("显示位置").defineInRange("position", 1, 1, 8);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST = BUILDER
            .comment("标识ID黑名单")
            .defineList("blacklist",
                    List.of("jei_skp", "emi_skp"),
                    entry -> entry instanceof String
            );

    static final ForgeConfigSpec SPEC = BUILDER.build();
}
