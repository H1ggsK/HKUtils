package com.h1ggsk.hkutils.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class Troll1 extends Command {

    public Troll1() {
        super("troll1", "Randomly rotates player, spams chat, makes them twerk, and crashes game.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            ClientPlayerEntity player = mc.player;
            if (player == null) return 0;

            Random random = new Random();

            // Rotation (20x every 250ms = 5s)
            new Thread(() -> {
                for (int i = 0; i < 20; i++) {
                    float yaw = random.nextFloat() * 360f - 180f;
                    float pitch = MathHelper.clamp(random.nextFloat() * 180f - 90f, -90f, 90f);
                    player.setYaw(yaw);
                    player.setPitch(pitch);
                    try { Thread.sleep(250); } catch (InterruptedException ignored) {}
                }
            }).start();

            // Chat spam (5x every 1s = 5s)
            new Thread(() -> {
                for (int i = 0; i < 5; i++) {
                    ChatUtils.sendPlayerMsg("I am a noob");
                    try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                }
            }).start();

            // Twerk (10x toggle = 5s)
            new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    mc.options.sneakKey.setPressed(true);
                    try { Thread.sleep(250); } catch (InterruptedException ignored) {}
                    mc.options.sneakKey.setPressed(false);
                    try { Thread.sleep(250); } catch (InterruptedException ignored) {}
                }
            }).start();

            // Crash game from main thread after 5s
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {}
                mc.execute(() -> {
                    MinecraftClient.getInstance().stop();
                    throw new RuntimeException("You got trolled lol");
                });
            }).start();

            return SINGLE_SUCCESS;
        });
    }
}
