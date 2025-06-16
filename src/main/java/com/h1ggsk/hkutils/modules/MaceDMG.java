package com.h1ggsk.hkutils.modules;

import com.h1ggsk.hkutils.HKUtils;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.HitResult;


/**
 * A module that deals maximum possible damage with a mace by sending extra movement packets
 * to bypass the attack cooldown.
 */
public class MaceDMG extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public MaceDMG() {
        super(HKUtils.HKUtils, "Mace-DMG", "A module that deals maximum possible damage with mace.");
    }

    private final Setting<Boolean> requireTarget = sgGeneral.add(new BoolSetting.Builder()
        .name("require-target")
        .description("Require your crosshair to be targeting an entity.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> forceStanding = sgGeneral.add(new BoolSetting.Builder()
        .name("force-standing")
        .description("Force player into standing position (if flying or swimming).")
        .defaultValue(true)
        .build()
    );

    @Override
    public void onActivate() {
        // Any initialization logic
    }

    @Override
    public void onDeactivate() {
        // Cleanup if needed
    }

    /**
     * When the player attacks an entity, send extra vertical movement packets to reset the attack cooldown.
     */
    @EventHandler
    private void onAttackEntity(AttackEntityEvent event) {
        if (requireTarget.get()) {
            if (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.ENTITY) return;
        }

        // Only activate when holding a mace
        if (mc.player.getMainHandStack().getItem() != Items.MACE) return;


        if (forceStanding.get()){
            if (mc.player.isGliding()) {
                mc.player.stopGliding();
            }
            if (mc.player.isSwimming()) {
                mc.player.setSwimming(false);
            }
        }

        // Send multiple position packets at different Y offsets
        for (int i = 0; i < 4; i++) {
            sendFakeY(0);
        }
        sendFakeY(Math.sqrt(500));
        sendFakeY(0);
    }

    private void sendFakeY(double offset)
    {
        mc.player.networkHandler.sendPacket(
            new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + offset,
                mc.player.getZ(), false, mc.player.horizontalCollision));
    }
}
