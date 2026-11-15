package com.mafuyu404.smartkeyprompts;

import com.google.gson.JsonParser;
import com.mafuyu404.smartkeyprompts.data.KeyPromptData;
import com.mafuyu404.smartkeyprompts.data.KeyPromptDataExtractor;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PromptDataTest {
    @Test
    void bundledSharedRulesDecodeAndRoundTrip() throws Exception {
        for (String name : List.of("tacz", "immersive_aircraft")) {
            try (var input = getClass().getResourceAsStream("/data/smartkeyprompts/key_prompts/" + name + ".json")) {
                assertNotNull(input, name);
                var json = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8));
                var data = KeyPromptData.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();
                assertFalse(data.entries().isEmpty());
                assertTrue(data.vars().containsKey("modLoaded"));
                var encoded = KeyPromptData.CODEC.encodeStart(JsonOps.INSTANCE, data).result().orElseThrow();
                assertEquals(data, KeyPromptData.CODEC.parse(JsonOps.INSTANCE, encoded).result().orElseThrow());
            }
        }
    }

    @Test
    void missingRequiredRuleFieldsAreRejected() {
        var result = KeyPromptData.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"modid\":\"test\"}"));
        assertTrue(result.error().isPresent());
    }

    @Test
    void extractorIncludesVariablesConditionsAndActionsWithoutDuplicates() {
        var data = new KeyPromptData("test", Map.of("vehicle", "vehicleType()"),
                List.of(new KeyPromptData.Entry(Map.of("vehicle", "vehicleType()"), List.of("show('test', 'key.jump')"))));
        var extractor = new KeyPromptDataExtractor();
        assertEquals(data.vars(), extractor.extractVariables(data));
        assertEquals(Set.of("vehicleType()", "show('test', 'key.jump')"), extractor.extractAllExpressions(data));
        assertEquals(Set.of(), extractor.extractAllExpressions(new KeyPromptData("test", null, null)));
    }
}
