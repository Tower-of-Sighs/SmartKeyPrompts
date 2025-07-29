package com.mafuyu404.smartkeyprompts.init;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;


public class ModKeybindings {
    public static KeyMapping CONTROL_KEY = new KeyMapping(
            "key.smartkeyprompts.control",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K,
            "key.categories.smartkeyprompts"
    );

    public static void register() {
        KeyBindingHelper.registerKeyBinding(CONTROL_KEY);
    }

}
