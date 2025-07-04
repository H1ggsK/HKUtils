package com.h1ggsk.hkutils.mixin;

import com.h1ggsk.hkutils.modules.AutoTotemLegit;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.Items;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Inject(method = "onMouseClick*", at = @At("HEAD"))
    private void onMouseClick(Slot slot, int slotId, int button, net.minecraft.screen.slot.SlotActionType actionType, CallbackInfo ci) {
        if (slot == null || slot.getStack().isEmpty()) return;

        if (slot.getStack().getItem() == Items.TOTEM_OF_UNDYING && slotId == 45) { // 45 is the offhand slot
            AutoTotemLegit mod = Modules.get().get(AutoTotemLegit.class);
            if (mod != null) mod.notifyManualMove();
        }
    }
}
