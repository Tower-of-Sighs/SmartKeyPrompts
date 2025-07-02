package com.mafuyu404.smartkeyprompts;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

//    private static final ForgeConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER.comment("Whether to log the dirt block on common setup").define("logDirtBlock", true);

    public static final ForgeConfigSpec.DoubleValue SCALE = BUILDER.comment("缩放").defineInRange("scale", 0.8, 0, 10);

    public static final ForgeConfigSpec.IntValue POSITION = BUILDER.comment("显示位置").defineInRange("position", 1, 1, 8);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>>  BLACKLIST  = BUILDER
            .comment("标识ID黑名单")
            .defineList("blacklist",
                    List.of("jei"),
                    entry -> entry instanceof String
            );

    static final ForgeConfigSpec SPEC = BUILDER.build();
}
