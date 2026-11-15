package com.mafuyu404.smartkeyprompts;

import cc.sighs.oelib.data.mvel.gen.ExpressionFunctionsRegistrar;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionSurfaceTest {
    @Test
    void bundledRulesUseFunctionsRegisteredByTheCompiledTarget() throws Exception {
        var functions = new HashSet<String>();
        var registrars = ServiceLoader.load(ExpressionFunctionsRegistrar.class).stream()
                .filter(provider -> provider.type().getName().startsWith("com.mafuyu404.smartkeyprompts."))
                .toList();
        assertFalse(registrars.isEmpty(), "Annotation processor must generate a discoverable registrar");
        for (var provider : registrars) {
            provider.get().register((name, owner, method, parameters) -> {
                assertTrue(functions.add(name), "Duplicate expression: " + name);
                assertDoesNotThrow(() -> owner.getMethod(method, parameters));
            }, null);
        }
        assertTrue(functions.contains("show"));
        assertTrue(functions.contains("isKeyPressedOfDesc"));
        assertNotEquals(functions.contains("hasMainHandNBT"), functions.contains("hasMainHandComponent"),
                "Each target exposes exactly one item representation");
        assertTrue(functions.contains("isModLoaded"));
        var call = Pattern.compile("(?<![.\\w])([A-Za-z_]\\w*)\\s*\\(");
        for (String name : List.of("tacz", "immersive_aircraft", "diligent_stalker")) {
            try (var input = getClass().getResourceAsStream("/data/smartkeyprompts/key_prompts/" + name + ".json")) {
                if (input == null && name.equals("diligent_stalker")) continue; // Standalone common has no target resources.
                assertNotNull(input, name);
                var data = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
                var expressions = new HashSet<String>();
                data.getAsJsonObject("vars").entrySet().forEach(entry -> expressions.add(entry.getValue().getAsString()));
                for (var entry : data.getAsJsonArray("entries")) {
                    entry.getAsJsonObject().getAsJsonObject("when").entrySet()
                            .forEach(condition -> expressions.add(condition.getValue().getAsString()));
                    entry.getAsJsonObject().getAsJsonArray("then")
                            .forEach(action -> expressions.add(action.getAsString()));
                }
                for (var expression : expressions) {
                    var matcher = call.matcher(expression);
                    while (matcher.find()) {
                        assertTrue(functions.contains(matcher.group(1)), name + ": " + expression);
                    }
                }
            }
        }
    }
}
