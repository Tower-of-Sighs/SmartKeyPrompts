package com.mafuyu404.smartkeyprompts;

import com.mafuyu404.smartkeyprompts.util.SkpUtils;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;

public class SmartKeyPromptsJSPlugin implements KubeJSPlugin {

    @Override
    public void registerBindings(BindingRegistry event) {
        event.add("SkpUtils", SkpUtils.class);
    }
}
