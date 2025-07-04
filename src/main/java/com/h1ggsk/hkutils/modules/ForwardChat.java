package com.h1ggsk.hkutils.modules;

import com.h1ggsk.hkutils.HKUtils;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ForwardChat extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> webhookUrl = sgGeneral.add(new StringSetting.Builder()
        .name("webhook-url")
        .description("The Discord webhook URL to forward messages to")
        .defaultValue("")
        .build()
    );

    private final Setting<String> chatColor = sgGeneral.add(new StringSetting.Builder()
        .name("chat-color")
        .description("The color for chat messages (hex or color name)")
        .defaultValue("gray")
        .build()
    );

    private ExecutorService executor;
    private final AtomicBoolean active = new AtomicBoolean(false);

    public ForwardChat() {
        super(HKUtils.HKUtils, "Forward-Chat", "A module that forwards all server messages to a Discord webhook of your choice!");
    }

    @Override
    public void onActivate() {
        executor = Executors.newSingleThreadExecutor();
        active.set(true);
    }

    @Override
    public void onDeactivate() {
        active.set(false);
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    @EventHandler
    private void onMessageSend(SendMessageEvent event) {
        if (mc.player == null || !active.get()) return;

        String eventMessage = event.message;
        String formattedMessage = String.format("`%s`: %s", mc.player.getName().getString(), eventMessage);

        try {
            executor.execute(() -> sendToWebhook(formattedMessage));
        } catch (Exception e) {
            // Handle case where executor might be shutting down
            mc.player.sendMessage(Text.of("Failed to forward message: " + e.getMessage()), false);
        }

        // Create styled message
        MutableText text = Text.literal("[Chat Forward]: ").setStyle(Style.EMPTY.withColor(parseColor(chatColor.get())))
            .append(Text.literal("<" + mc.player.getName().getString() + "> ").formatted(Formatting.WHITE))
            .append(Text.literal(eventMessage).formatted(Formatting.WHITE));

        mc.player.sendMessage(text, false);
        event.cancel();
    }

    private TextColor parseColor(String color) {
        // Try to parse as hex first
        if (color.startsWith("#")) {
            try {
                return TextColor.fromRgb(Integer.parseInt(color.substring(1), 16));
            } catch (NumberFormatException ignored) {}
        }

        // Try to parse as named color
        Formatting formatting = Formatting.byName(color.toUpperCase());
        if (formatting != null && formatting.isColor()) {
            return TextColor.fromFormatting(formatting);
        }

        // Default to gray if parsing fails
        return TextColor.fromFormatting(Formatting.GRAY);
    }

    private void sendToWebhook(String message) {
        if (webhookUrl.get().isEmpty()) return;

        try {
            URL url = new URL(webhookUrl.get());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonPayload = String.format("{\"content\":\"%s\"}", message.replace("\"", "\\\""));
            connection.getOutputStream().write(jsonPayload.getBytes(StandardCharsets.UTF_8));

            connection.getInputStream().close(); // We don't care about the response
        } catch (Exception e) {
            mc.execute(() ->
                mc.player.sendMessage(Text.of("Failed to send message: " + e.getMessage()), false)
            );
        }
    }
}
