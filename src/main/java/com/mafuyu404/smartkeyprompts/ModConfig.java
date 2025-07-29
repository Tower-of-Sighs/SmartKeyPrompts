package com.mafuyu404.smartkeyprompts;


import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.ArrayList;
import java.util.List;

@Config(name = "smartkeyprompts")
public class ModConfig implements ConfigData {

    @Comment("缩放")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 10)
    public double scale = 0.8;

    @Comment("显示位置")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 8)
    public int position = 1;

    @Comment("标识ID黑名单")
    public List<String> blacklist = new ArrayList<>(List.of("jei_skp"));

    // 获取配置实例的静态方法
    public static ModConfig getInstance() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public static void register() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
    }
}