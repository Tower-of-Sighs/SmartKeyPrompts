package com.mafuyu404.smartkeyprompts.data;

import java.util.List;
import java.util.Map;

public record KeyPromptData(
        String modid,
        Map<String, String> vars,
        List<Entry> entries
) {
    public record Entry(
            Map<String, String> when,
            List<String> then
    ) {
    }
}