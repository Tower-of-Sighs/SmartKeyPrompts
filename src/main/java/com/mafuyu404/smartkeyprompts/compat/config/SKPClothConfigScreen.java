package com.mafuyu404.smartkeyprompts.compat.config;

import com.mafuyu404.smartkeyprompts.Config;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.network.chat.Component;

import java.util.List;

@SuppressWarnings("unchecked")
public class SKPClothConfigScreen {
    private SKPClothConfigScreen() {
    }

    public static ConfigBuilder getConfigBuilder() {
        var root = ConfigBuilder.create().setTitle(Component.translatable("config.smartkeyprompts.title"));
        root.setGlobalized(true);
        root.setGlobalizedExpanded(false);
        var entry = root.entryBuilder();

        var category = root.getOrCreateCategory(Component.translatable("config.smartkeyprompts.category"));
        category.addEntry(entry.startDoubleField(
                        Component.translatable("config.smartkeyprompts.category.scale"),
                        Config.SCALE.get())
                .setDefaultValue(0.8)
                .setMin(0.0)
                .setMax(10.0)
                .setTooltip(Component.translatable("config.smartkeyprompts.category.scale.tooltip"))
                .setSaveConsumer(Config.SCALE::set)
                .build());
        category.addEntry(entry.startIntSlider(
                        Component.translatable("config.smartkeyprompts.category.position"),
                        Config.POSITION.get(),
                        1,
                        8)
                .setDefaultValue(1)
                .setTooltip(Component.translatable("config.smartkeyprompts.category.position.tooltip"))
                .setSaveConsumer(Config.POSITION::set)
                .build());
        category.addEntry(entry.startStrList(
                        Component.translatable("config.smartkeyprompts.category.blacklist"),
                        (List<String>) Config.BLACKLIST.get())
                .setDefaultValue(
                        List.of(
                                "jei_skp",
                                "emi_skp"
                        )
                )
                .setTooltip(Component.translatable("config.smartkeyprompts.category.blacklist.tooltip"))
                .setSaveConsumer(Config.BLACKLIST::set)
                .build());
        return root;
    }
}
