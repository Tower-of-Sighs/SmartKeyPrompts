package com.mafuyu404.smartkeyprompts.mixin;

import com.mafuyu404.smartkeyprompts.init.ConfigAction;
import com.mafuyu404.smartkeyprompts.init.ModKeybindings;
import com.mafuyu404.smartkeyprompts.util.KeyUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "onScroll", at = @At("RETURN"))
    private void ddd(long p_91527_, double p_91528_, double offset, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != null && minecraft.player != null) {
            if (!KeyUtils.isKeyPressed(ModKeybindings.CONTROL_KEY.getKey().getValue())) return;
            ConfigAction.scaleHUD(offset);
        }
    }
}
