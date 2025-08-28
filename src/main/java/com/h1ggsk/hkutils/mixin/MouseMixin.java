package com.h1ggsk.hkutils.mixin;

import com.h1ggsk.hkutils.event.MouseUpdateEvent;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Mouse.class})
public abstract class MouseMixin {
    @Shadow
    private double cursorDeltaX;
    @Shadow
    private double cursorDeltaY;

    @Inject(at = @At("HEAD"), method = "tick()V")
    private void onTick(CallbackInfo ci) {
        MouseUpdateEvent event = MouseUpdateEvent.set(cursorDeltaX, cursorDeltaY);
        MeteorClient.EVENT_BUS.post(event);
        cursorDeltaX = event.getDeltaX();
        cursorDeltaY = event.getDeltaY();
    }
}
