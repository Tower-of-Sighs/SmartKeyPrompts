package com.mafuyu404.smartkeyprompts.data;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = SmartKeyPrompts.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class KeyPromptDatapack {
    public static final ResourceKey<Registry<KeyPromptData>> KEY_PROMPT_REGISTRY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(SmartKeyPrompts.MOD_ID, "key_prompts"));

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(KEY_PROMPT_REGISTRY, KeyPromptData.CODEC, KeyPromptData.CODEC);
    }
}
