package com.mafuyu404.smartkeyprompts;

import com.mafuyu404.smartkeyprompts.utils.SkpUtil;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import net.neoforged.fml.loading.FMLEnvironment;

public class SmartKeyPromptsJSPlugin implements KubeJSPlugin {

    @Override
    public void registerBindings(BindingRegistry bindings) {
        if (FMLEnvironment.dist.isClient()) {
            bindings.add("SkpUtil", SkpUtil.class);
        }
    }
}
