package com.h1ggsk.hkutils.modules;

import com.h1ggsk.hkutils.HKUtils;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.math.Vec3d;

public class SpeedLimiter extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> maxBPS = sgGeneral.add(new DoubleSetting.Builder()
        .name("max-bps")
        .description("Maximum movement speed in blocks per second (3D).")
        .defaultValue(34.0)
        .min(0.1)
        .sliderMax(100.0)
        .build()
    );

    // Tracks last tick’s position of whichever entity is moving us
    private Vec3d lastPos;
    private Entity lastEntity;

    public SpeedLimiter() {
        super(HKUtils.HKUtils, "speed-limiter", "Caps ALL movement (3D) at your chosen BPS.");
    }

    @Override
    public void onActivate() {
        if (mc.player != null) {
            lastEntity = mc.player.hasVehicle() ? mc.player.getVehicle() : mc.player;
            lastPos    = lastEntity.getPos();
        }
    }

    @Override
    public void onDeactivate() {
        lastEntity = null;
        lastPos    = null;
    }

    @EventHandler
    private void onGameJoin(PacketEvent.Receive event) {
        if (event.packet instanceof GameJoinS2CPacket) {
            // Reset internal state
            lastPos = null;
            lastEntity = null;

            // Reinitialize if the module is active
            if (isActive() && mc.player != null) {
                lastEntity = mc.player.hasVehicle() ? mc.player.getVehicle() : mc.player;
                lastPos = lastEntity.getPos();
            }
        }
    }


    /** Movement cap runs last, as before. */
    @EventHandler(priority = Integer.MIN_VALUE)
    private void onTick(TickEvent.Post event) {
        if (mc.player == null) return;

        Entity mover = mc.player.hasVehicle() ? mc.player.getVehicle() : mc.player;
        Vec3d curr = mover.getPos();

        if (lastPos == null || mover != lastEntity) {
            lastEntity = mover;
            lastPos    = curr;
            return;
        }

        Vec3d delta = curr.subtract(lastPos);
        double distance   = delta.length();
        double currentBPS = distance * 20;
        double cap        = maxBPS.get();

        if (currentBPS > cap) {
            double factor       = cap / currentBPS;
            Vec3d scaledDelta   = delta.multiply(factor);
            Vec3d newPos        = lastPos.add(scaledDelta);
            mover.setPosition(newPos.x, newPos.y, newPos.z);

            Vec3d vel = mover.getVelocity();
            mover.setVelocity(vel.x * factor, vel.y * factor, vel.z * factor);
        }

        lastPos = mover.getPos();
    }
}
