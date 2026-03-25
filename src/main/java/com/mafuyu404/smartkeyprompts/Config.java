package com.mafuyu404.smartkeyprompts;

import cc.sighs.oelib.config.ConfigAccess;
import cc.sighs.oelib.config.ConfigManager;
import cc.sighs.oelib.config.ConfigRecordCodecBuilder;
import cc.sighs.oelib.config.ConfigUnit;
import cc.sighs.oelib.config.field.ConfigField;
import cc.sighs.oelib.config.model.ConfigStorageFormat;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record Config(
        double scale,
        int position,
        List<String> blacklist
) {

    private static final String FILE_NAME = "smartkeyprompts-client";

    public static final ConfigUnit<Config> UNIT = ConfigRecordCodecBuilder.createClient(
            new ResourceLocation(SmartKeyPrompts.MODID, "smart_key_prompts"),
            instance -> instance.group(
                    ConfigField.doubleRange("scale", 0.0, 10.0)
                            .defaultValue(0.8)
                            .text()
                            .comment("""
                                    #缩放
                                    # Default: 0.8
                                    # Range: 0.0 ~ 10.0
                                    """)
                            .forGetter(Config::scale),
                    ConfigField.intRange("position", 1, 8)
                            .defaultValue(1)
                            .text()
                            .comment("""
                                    #显示位置
                                    # Default: 1
                                    # Range: 1 ~ 8
                                    """)
                            .forGetter(Config::position),
                    ConfigField.list("blacklist", Codec.STRING)
                            .defaultValue(List.of("jei_skp", "emi_skp"))
                            .comment("#标识ID黑名单")
                            .forGetter(Config::blacklist)
            ).apply(instance, Config::new),
            meta -> meta
                    .fileName(FILE_NAME)
                    .format(ConfigStorageFormat.TOML)
    );

    public static final ConfigAccess<Config> ACCESS = new ConfigAccess<>(UNIT);

    public static Config get() {
        return UNIT.get();
    }

    public static void register() {
        ConfigManager.registerClient(UNIT);
    }
}