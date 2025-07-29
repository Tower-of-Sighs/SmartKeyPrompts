package com.mafuyu404.smartkeyprompts;

import com.mafuyu404.smartkeyprompts.util.SkpUtils;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class SmartKeyPromptsJSPlugin extends KubeJSPlugin {

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("SkpUtils", SkpUtils.class);
    }
}
