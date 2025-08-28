package com.h1ggsk.hkutils.modules;

import com.h1ggsk.hkutils.HKUtils;
import com.h1ggsk.hkutils.event.MouseUpdateEvent;
import com.h1ggsk.hkutils.rotation.Rotation;
import com.h1ggsk.hkutils.rotation.RotationUtils;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Tameable;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Random;
import java.util.Set;

public final class KillauraLegit extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .description("Maximum attack range.")
        .defaultValue(4.25)
        .min(1.0)
        .max(6.0)
        .build()
    );

    private final Setting<Double> rotationSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("rotation-speed")
        .description("Rotation speed towards targets.")
        .defaultValue(600.0)
        .min(10.0)
        .max(3600.0)
        .build()
    );

    private final Setting<Double> fov = sgGeneral.add(new DoubleSetting.Builder()
        .name("fov")
        .description("Field of view in degrees.")
        .defaultValue(360.0)
        .min(30.0)
        .max(360.0)
        .build()
    );

    private final Setting<Double> minDelay = sgGeneral.add(new DoubleSetting.Builder()
        .name("min-delay")
        .description("Minimum delay between attacks (ms).")
        .defaultValue(20.0)
        .min(1.0)
        .max(100.0)
        .build()
    );

    private final Setting<Double> maxDelay = sgGeneral.add(new DoubleSetting.Builder()
        .name("max-delay")
        .description("Maximum delay between attacks (ms).")
        .defaultValue(70.0)
        .min(1.0)
        .max(100.0)
        .build()
    );

    private final Setting<Boolean> targetInvis = sgGeneral.add(new BoolSetting.Builder()
        .name("target-invis")
        .description("Attack invisible entities.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> ignoreTamed = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-tamed")
        .description("Will avoid attacking mobs you tamed.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> ignoreNamed = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-named")
        .description("Whether or not to attack mobs with a name.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Set<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
        .name("entities")
        .description("Entities to attack.")
        .onlyAttackable()
        .defaultValue(EntityType.PLAYER)
        .build()
    );

    private Entity target;
    private float serverYaw;
    private float serverPitch;
    private long lastAttackTime;
    private final Random random = new Random();

    public KillauraLegit() {
        super(HKUtils.HKUtils, "KillauraLegit", "Attacks entites near you");
    }

    @Override
    public void onActivate() {
        target = null;
        lastAttackTime = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.currentScreen instanceof HandledScreen) {
            target = null;
            return;
        }

        target = null;
        double closestDistance = Double.MAX_VALUE;
        double rangeSq = range.get() * range.get();

        for (Entity entity : mc.world.getEntities()) {
            if (ignoreTamed.get()) {
                if (entity instanceof Tameable tameable
                    && tameable.getOwner() != null
                    && tameable.getOwner().equals(mc.player)
                ) continue;
            }
            if (ignoreNamed.get() && entity.hasCustomName()) continue;
            if (!entities.get().contains(entity.getType()) || entity == mc.player) continue;
            if (!targetInvis.get() && entity.isInvisible()) continue;

            double distance = mc.player.squaredDistanceTo(entity);
            if (distance > rangeSq) continue;

            if (fov.get() < 360.0 && getAngleToEntity(entity) > fov.get() / 2.0) continue;

            if (!hasLineOfSight(entity.getBoundingBox().getCenter())) continue;

            if (distance < closestDistance) {
                closestDistance = distance;
                target = entity;
            }
        }

        if (target != null) {
            Box box = target.getBoundingBox();
            Rotation needed = RotationUtils.getNeededRotations(box.getCenter());
            Rotation next = RotationUtils.slowlyTurnTowards(needed, rotationSpeed.get().floatValue() / 20F);
            serverYaw = next.yaw();
            serverPitch = next.pitch();

            if (canAttack() && (RotationUtils.isAlreadyFacing(needed) || RotationUtils.isFacingBox(box, range.get()))) {
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
                lastAttackTime = System.currentTimeMillis() + getRandomDelay();
            }
        }
    }

    private boolean canAttack() {
        if (target == null) return false;
        if (mc.player.getAttackCooldownProgress(0.5f) < 0.9f) return false;
        return System.currentTimeMillis() >= lastAttackTime;
    }

    @EventHandler
    private void onMouseUpdate(MouseUpdateEvent event) {
        if (target == null || mc.player == null) return;

        float yawDiff = MathHelper.wrapDegrees(serverYaw - mc.player.getYaw());
        float pitchDiff = MathHelper.wrapDegrees(serverPitch - mc.player.getPitch());

        if (Math.abs(yawDiff) < 1 && Math.abs(pitchDiff) < 1) return;

        event.deltaX += (int) yawDiff;
        event.deltaY += (int) pitchDiff;
    }

    private long getRandomDelay() {
        return (long) (minDelay.get() + random.nextDouble() * (maxDelay.get() - minDelay.get()));
    }

    private float getAngleToEntity(Entity entity) {
        Box box = entity.getBoundingBox();
        double dx = box.getCenter().x - mc.player.getX();
        double dz = box.getCenter().z - mc.player.getZ();
        float yaw = (float) (MathHelper.atan2(dz, dx) * 180.0 / Math.PI - 90.0);
        return Math.abs(MathHelper.wrapDegrees(yaw - mc.player.getYaw()));
    }

    public static boolean hasLineOfSight(Vec3d to) {
        return raycast(getEyesPos(), to).getType() == HitResult.Type.MISS;
    }

    public static Vec3d getEyesPos() {
        float eyeHeight = MeteorClient.mc.player.getEyeHeight(MeteorClient.mc.player.getPose());
        return MeteorClient.mc.player.getPos().add(0, eyeHeight, 0);
    }

    public static BlockHitResult raycast(Vec3d from, Vec3d to) {
        return raycast(from, to, RaycastContext.FluidHandling.NONE);
    }

    public static BlockHitResult raycast(Vec3d from, Vec3d to, RaycastContext.FluidHandling fluidHandling) {
        RaycastContext context = new RaycastContext(from, to, RaycastContext.ShapeType.COLLIDER, fluidHandling, MeteorClient.mc.player);
        return MeteorClient.mc.world.raycast(context);
    }
}
