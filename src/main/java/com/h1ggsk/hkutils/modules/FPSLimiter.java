package com.h1ggsk.hkutils.modules;

import com.h1ggsk.hkutils.HKUtils;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class FPSLimiter extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> maxFps = sgGeneral.add(new IntSetting.Builder()
        .name("max-fps")
        .description("The maximum FPS to limit to.")
        .defaultValue(20)
        .min(1)
        .sliderMax(600)
        .build()
    );

    private long lastFrameTime = 0;

    public FPSLimiter() {
        super(HKUtils.HKUtils, "FPS-Limiter", "A module that manually limits FPS.");
    }

    @EventHandler
    private void onRender2D(Render2DEvent event) {
        if (!isActive()) return;

        long frameDuration = 1_000_000_000L / maxFps.get();
        long currentTime = System.nanoTime();
        long timeSinceLastFrame = currentTime - lastFrameTime;

        if (timeSinceLastFrame < frameDuration) {
            long sleepTimeMillis = (frameDuration - timeSinceLastFrame) / 1_000_000L;
            int sleepTimeNanos = (int) ((frameDuration - timeSinceLastFrame) % 1_000_000L);

            try {
                Thread.sleep(sleepTimeMillis, sleepTimeNanos);
            } catch (InterruptedException ignored) {}
        }

        lastFrameTime = System.nanoTime();
    }

    @Override
    public void onActivate() {
        lastFrameTime = System.nanoTime();
    }
}
