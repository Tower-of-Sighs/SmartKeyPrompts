package com.mafuyu404.smartkeyprompts;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class Config {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue SCALE = BUILDER.comment("缩放").defineInRange("scale", 0.8, 0, 10);

    public static final ModConfigSpec.IntValue POSITION = BUILDER.comment("显示位置").defineInRange("position", 1, 1, 8);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLACKLIST = BUILDER
            .comment("标识ID黑名单")
            .defineList("blacklist",
                    List.of("jei_skp", "emi_skp"),
                    entry -> entry instanceof String
            );

    static final ModConfigSpec SPEC = BUILDER.build();
}
