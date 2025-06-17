package com.h1ggsk.hkutils.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class H1ggsKPing extends Command {

    public H1ggsKPing() {
        super("ping", "Pings h1ggsk (please don't spam!).");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            try {
                assert mc.player != null;
                String playerName = mc.player.getName().getString();

                // URL encode the content
                String message = URLEncoder.encode("<@1276279640465477703>, you have been pinged by " + playerName + "!", StandardCharsets.UTF_8);

                // Create the URL with GET parameters
                URL url = new URL("https://ping.h1ggsk.workers.dev/?content=" + message);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    info("h1ggsk has been pinged successfully!");
                } else {
                    error("Failed to send ping. Server returned HTTP code: " + responseCode);
                }
            } catch (Exception e) {
                error("Failed to send ping: " + e.getMessage());
            }
            return SINGLE_SUCCESS;
        });
    }
}
