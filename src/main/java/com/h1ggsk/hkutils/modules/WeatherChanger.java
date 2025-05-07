package com.h1ggsk.hkutils.modules;

import com.h1ggsk.hkutils.HKUtils;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;

public class WeatherChanger extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public enum WeatherMode {
        Clear, Rain, Thunder
    }

    private final Setting<WeatherMode> weather = sgGeneral.add(new EnumSetting.Builder<WeatherMode>()
        .name("weather")
        .description("The weather to set.")
        .defaultValue(WeatherMode.Clear)
        .build()
    );

    private final Setting<Boolean> spoof = sgGeneral.add(new BoolSetting.Builder()
        .name("spoof-server")
        .description("Prevents the server from changing your weather.")
        .defaultValue(true)
        .build()
    );

    private float serverRainGradient = 0f;
    private float serverThunderGradient = 0f;

    public WeatherChanger() {
        super(HKUtils.HKUtils, "weather-changer", "Lets you change the weather locally.");
    }

    @Override
    public void onActivate() {
        if (mc.world != null) {
            // Store the current server weather state
            serverRainGradient = mc.world.getRainGradient(1.0f);
            serverThunderGradient = mc.world.getThunderGradient(1.0f);
        }
    }

    @Override
    public void onDeactivate() {
        if (mc.world != null) {
            // Restore the server's weather state
            mc.world.setRainGradient(serverRainGradient);
            mc.world.setThunderGradient(serverThunderGradient);
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.world == null) return;

        switch (weather.get()) {
            case Clear -> {
                mc.world.setRainGradient(0);
                mc.world.setThunderGradient(0);
            }
            case Rain -> {
                mc.world.setRainGradient(1);
                mc.world.setThunderGradient(0);
            }
            case Thunder -> {
                mc.world.setRainGradient(1);
                mc.world.setThunderGradient(1);
            }
        }
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (!spoof.get()) return;

        if (event.packet instanceof GameStateChangeS2CPacket packet) {
            GameStateChangeS2CPacket.Reason reason = packet.getReason();

            // Cancel server weather change packets
            if (reason == GameStateChangeS2CPacket.RAIN_STARTED
                || reason == GameStateChangeS2CPacket.RAIN_STOPPED
                || reason == GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED
                || reason == GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED) {
                event.cancel();
            }
        }
    }
}
