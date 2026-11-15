package com.mafuyu404.smartkeyprompts.mixin;

import com.mafuyu404.smartkeyprompts.event.InputEvent;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("HEAD"))
    private void skp$onKey(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        var window = this.minecraft.getWindow();
        if (windowPointer != window.getWindow()) return;
        InputEvent.KEY.invoker().onKey(key, scanCode, action, modifiers);
    }
}