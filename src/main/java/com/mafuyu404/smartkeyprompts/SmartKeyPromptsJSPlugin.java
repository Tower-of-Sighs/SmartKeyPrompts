package com.mafuyu404.smartkeyprompts;

import com.mafuyu404.smartkeyprompts.util.*;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class SmartKeyPromptsJSPlugin extends KubeJSPlugin {

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("SKP$KeyUtils", KeyUtils.class);
        event.add("SKP$NBTUtils", NBTUtils.class);
        event.add("SKP$PlayerUtils", PlayerUtils.class);
        event.add("SKP$PromptUtils", PromptUtils.class);
        event.add("SKP$CommonUtils", CommonUtils.class);
    }
}
