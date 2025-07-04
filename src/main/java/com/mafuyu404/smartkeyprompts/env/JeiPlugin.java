package com.mafuyu404.smartkeyprompts.env;

import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Environment(EnvType.CLIENT)
@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {
    private static IJeiRuntime jeiRuntime;

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(SmartKeyPrompts.MODID, "jei_plugin");
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        JeiPlugin.jeiRuntime = jeiRuntime;
    }

    public static Optional<IJeiRuntime> getJeiRuntime() {
        return Optional.ofNullable(jeiRuntime);
    }

    public static boolean isEnabled() {
        final boolean[] result = {false};
        JeiPlugin.getJeiRuntime().ifPresent(jeiRuntime -> {
            result[0] = jeiRuntime.getIngredientListOverlay().isListDisplayed() && Minecraft.getInstance().screen != null;
        });
        return result[0];
    }
}