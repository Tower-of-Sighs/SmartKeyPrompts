package com.mafuyu404.smartkeyprompts.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mafuyu404.smartkeyprompts.util.KeyUtils;
import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InputConstants.Type.class)
public class InputConstants$TypeMixin {
    @Shadow @Final private Int2ObjectMap<InputConstants.Key> map;

    public Int2ObjectMap<InputConstants.Key> getMap() {
        return map;
    }

    @Inject(method = "addKey", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;put(ILjava/lang/Object;)Ljava/lang/Object;"), remap = false)
    private static void q(InputConstants.Type p_84900_, String key, int code, CallbackInfo ci, @Local InputConstants.Key inputconstants$key) {
        KeyUtils.addKeyMap(key, code);
        if (code == 1) KeyUtils.unknownKey = inputconstants$key;
    }
}
