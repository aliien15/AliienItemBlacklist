package com.aliiensmp.aliienItemBlacklist.utils;

import com.aliiensmp.aliienItemBlacklist.AliienItemBlacklist;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class UpdateChecker {

    private final AliienItemBlacklist plugin;
    private final String versionUrl;

    public UpdateChecker(AliienItemBlacklist plugin, String versionUrl) {
        this.plugin = plugin;
        this.versionUrl = versionUrl;
    }

    public void getVersion(final Consumer<String> consumer) {
        CompletableFuture.runAsync(() -> {
            try {
                URL url = new URI(this.versionUrl).toURL();
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
