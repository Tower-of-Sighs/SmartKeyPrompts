package com.mafuyu404.smartkeyprompts.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mafuyu404.smartkeyprompts.event.InputEvent;
import com.mafuyu404.smartkeyprompts.init.ConfigAction;
import com.mafuyu404.smartkeyprompts.init.ModKeybindings;
import com.mafuyu404.smartkeyprompts.util.KeyUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @Shadow
    private double xpos;
    @Shadow
    private double ypos;

    @Shadow
    public abstract boolean isLeftPressed();

    @Shadow
    public abstract boolean isMiddlePressed();

    @Shadow
    public abstract boolean isRightPressed();

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
            method = "onPress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;",
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void skp$onMouseButtonPre(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
        boolean cancel = InputEvent.MOUSE_BUTTON_PRE.invoker().onMouseButtonPre(button, action, modifiers);
        if (cancel) {
            ci.cancel();
        }
    }

    @Inject(method = "onPress", at = @At("TAIL"))
    private void skp$onMouseButtonPost(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
        var window = this.minecraft.getWindow();
        if (windowPointer != window.getWindow()) return;
        InputEvent.MOUSE_BUTTON_POST.invoker().onMouseButtonPost(button, action, modifiers);
    }

    @Inject(
            method = "onScroll",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void skp$onScroll(long window, double xOffset, double yOffset, CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) double g, @Local(ordinal = 1, argsOnly = true) double h) {
        if (InputEvent.MOUSE_SCROLL.invoker().onMouseScroll(
                g, h, xpos, ypos,
                isLeftPressed(), isMiddlePressed(), isRightPressed()
        )) {
            ci.cancel();
        }
    }

    @Inject(method = "onScroll", at = @At("RETURN"))
    private void ddd(long p_91527_, double p_91528_, double offset, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != null && minecraft.player != null) {
            if (!KeyUtils.isKeyPressed(ModKeybindings.CONTROL_KEY.key.getValue())) return;
            ConfigAction.scaleHUD(offset);
        }
    }
}
