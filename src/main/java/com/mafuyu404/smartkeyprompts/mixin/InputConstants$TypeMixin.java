package com.mafuyu404.smartkeyprompts.mixin;

import com.mafuyu404.smartkeyprompts.util.KeyUtils;
import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.mojang.blaze3d.platform.InputConstants$Type")
public class InputConstants$TypeMixin {
    @Shadow @Final private Int2ObjectMap<InputConstants.Key> map;

    public Int2ObjectMap<InputConstants.Key> getMap() {
        return map;
    }

    @Inject(method = "addKey", at = @At("HEAD"))
    private static void q(InputConstants.Type p_84900_, String key, int code, CallbackInfo ci) {
        KeyUtils.addKeyMap(key, code);
    }
}
