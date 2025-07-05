package com.mafuyu404.smartkeyprompts.data;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

@Mod.EventBusSubscriber(modid = SmartKeyPrompts.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyPromptDatapack {
    public static final ResourceKey<Registry<KeyPromptData>> KEY_PROMPT_REGISTRY =
            ResourceKey.createRegistryKey(new ResourceLocation(SmartKeyPrompts.MODID, "key_prompts"));

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(KEY_PROMPT_REGISTRY, KeyPromptData.CODEC, KeyPromptData.CODEC);
    }
}