package com.mafuyu404.smartkeyprompts.mixin;

import com.mafuyu404.smartkeyprompts.util.KeyUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = KeyMapping.class)
public class KeyMappingMixin {
    @Shadow @Final private String name;

    @Inject(method = {"isDown", "consumeClick", "matches", "same"}, at = @At("RETURN"), cancellable = true)
    private void a(CallbackInfoReturnable<Boolean> cir) {
        if (KeyUtils.isKeyDisabled(this.name)) {
            cir.setReturnValue(false);
        }
    }

//    @Inject(method = "getKey", at = @At("HEAD"), cancellable = true, remap = false)
//    private void b(CallbackInfoReturnable<InputConstants.Key> cir) {
//        if (KeyUtils.isKeyDisabled(this.name)) {
//            cir.setReturnValue(KeyUtils.unknownKey);
//        }
//    }
}
