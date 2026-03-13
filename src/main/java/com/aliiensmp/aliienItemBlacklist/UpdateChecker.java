package com.aliiensmp.aliienItemBlacklist;

import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

    private final AliienItemBlacklist plugin;
    private final String versionUrl;

    public UpdateChecker(AliienItemBlacklist plugin, String versionUrl) {
        this.plugin = plugin;
        this.versionUrl = versionUrl;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                URL url = new URI(this.versionUrl).toURL();

                // Open the connection and read the text
                try (InputStream inputStream = url.openStream();
                     Scanner scanner = new Scanner(inputStream)) {

                    if (scanner.hasNext()) {
                        consumer.accept(scanner.next());
                    }
                }
            } catch (IOException | URISyntaxException exception) {
                plugin.getLogger().warning("Unable to check for updates: " + exception.getMessage());
            }
        });
    }
}
