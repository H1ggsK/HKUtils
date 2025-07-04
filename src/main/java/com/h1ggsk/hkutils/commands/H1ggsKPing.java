package com.h1ggsk.hkutils.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.net.URL;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutorService;

public class H1ggsKPing extends Command {

    private ExecutorService executor;

    public H1ggsKPing() {
        super("iamanannoyingpieceofshit", "Pings h1ggsk (please don't spam!)");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            try {
                executor.execute(this::ping);
            } catch (Exception e) {
                // Handle case where executor might be shutting down
                mc.player.sendMessage(Text.of("Failed to create thread to send message: " + e.getMessage()), false);
            }
            return SINGLE_SUCCESS;
        });
    }

    private void ping() {
        try {
            // Create the URL with GET parameters
            URL url = new URL("https://ping.h1ggsk.workers.dev/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                info("h1ggsk has been pinged!");
            } else if (responseCode == 429) {
                error("Failed to send ping, slow down on messages!");
            } else {
                error("Failed to send ping. Server returned HTTP code: " + responseCode);
            }
        } catch (Exception e) {
            error("Failed to send ping: " + e.getMessage());
        }
    }
}
