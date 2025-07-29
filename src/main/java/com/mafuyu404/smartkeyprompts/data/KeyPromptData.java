package com.mafuyu404.smartkeyprompts.data;

import com.mafuyu404.oelib.api.DataDriven;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Map;

@DataDriven(
        modid = "smartkeyprompts",
        folder = "key_prompts",
        syncToClient = true,
        supportArray = true
)
public record KeyPromptData(
        String modid,
        Map<String, String> vars,
        List<Entry> entries
) {
    public static final Codec<KeyPromptData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("modid").forGetter(KeyPromptData::modid),
                    Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("vars").forGetter(KeyPromptData::vars),
                    Entry.CODEC.listOf().fieldOf("entries").forGetter(KeyPromptData::entries)
            ).apply(instance, KeyPromptData::new)
    );

    public record Entry(
            Map<String, String> when,
            List<String> then
    ) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("when").forGetter(Entry::when),
                        Codec.STRING.listOf().fieldOf("then").forGetter(Entry::then)
                ).apply(instance, Entry::new)
        );
    }
}