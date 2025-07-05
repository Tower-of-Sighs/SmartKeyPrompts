package com.mafuyu404.smartkeyprompts;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class Config {
    public static final ModConfigSpec CONFIG_SPEC;

    public static final ModConfigSpec.DoubleValue SCALE;

    public static final ModConfigSpec.IntValue POSITION;

    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLACKLIST;

    static {
        ModConfigSpec.Builder CONFIG_BUILDER = new ModConfigSpec.Builder();
        CONFIG_BUILDER.push("config");
        SCALE = CONFIG_BUILDER.defineInRange("scale", 0.8, 0, 10);
        POSITION = CONFIG_BUILDER.defineInRange("position", 1, 1, 8);
        BLACKLIST = CONFIG_BUILDER.defineList("blacklist", List.of("jei_skp"), entry -> entry instanceof String);
        CONFIG_BUILDER.pop();
        CONFIG_SPEC = CONFIG_BUILDER.build();
    }
}
