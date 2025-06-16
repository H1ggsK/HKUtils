package com.h1ggsk.hkutils.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import net.minidev.json.JSONObject;


public class H1ggsKPing extends Command {

    public H1ggsKPing() {
        super("ping", "Pings h1ggsk (please don't spam!).");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            try {
                URL url = new URL("https://discord.com/api/webhooks/1383967962758905866/BOf6N4Ya3RfIlhZeNOJunTCFs4FLK4TwpoVz0DGvqNuUpv_q7N-n9J1flAklxZSv_11p");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject payload = new JSONObject();
                assert mc.player != null;
                String payloadContent = "<@1276279640465477703>, you have been pinged by " + mc.player.getName().getString() + "!";
                payload.put("content", payloadContent);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                connection.getResponseCode(); // Trigger the request
                info("h1ggsk has been pinged!");
            } catch (Exception e) {
                error("Failed to send ping: " + e.getMessage());
            }
            return SINGLE_SUCCESS;
        });
    }
}
