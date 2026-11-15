package com.mafuyu404.smartkeyprompts;

import com.mafuyu404.smartkeyprompts.util.*;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;

public class SmartKeyPromptsJSPlugin extends KubeJSPlugin {

    @Override
    public void registerBindings(BindingsEvent event) {
        var type = event.getType();
        if (type.equals(ScriptType.CLIENT)) {
            event.add("SKP$KeyUtils", KeyUtils.class);
            event.add("SKP$NBTUtils", NBTUtils.class);
            event.add("SKP$PlayerUtils", PlayerUtils.class);
            event.add("SKP$PromptUtils", PromptUtils.class);
            event.add("SKP$CommonUtils", CommonUtils.class);
        }
    }
}
