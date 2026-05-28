package com.aliiensmp.aliienItemBlacklist.services;

import com.aliiensmp.aliienItemBlacklist.AliienItemBlacklist;
import com.aliiensmp.aliienItemBlacklist.config.Settings;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class AlertLogger {

    private AliienItemBlacklist plugin;

    public AlertLogger(AliienItemBlacklist plugin) {
        this.plugin = plugin;
    }

    public void logBlacklistedItem(Player player, Material mat) {
        if (!Settings.ENABLE_LOGGING) return;

        CompletableFuture.runAsync(() -> {
            try {
                File dataFolder = plugin.getDataFolder();
                if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                    plugin.getLogger().warning("Could not create plugin data folder for logging!");
                    return;
                }

                File logFile = new File(dataFolder, "logs.txt");
                if (!logFile.exists() && !logFile.createNewFile()) {
                    plugin.getLogger().warning("Could not create logs.txt file!");
                    return;
                }

                String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
                String logMessage = "[" + timestamp + "] Player " + player.getName() + " had a " + mat.name() + " item.";

                try (FileWriter fw = new FileWriter(logFile, true);
                     PrintWriter pw = new PrintWriter(fw)) {
                    pw.println(logMessage);
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "An error occurred while writing to logs.txt!", e);
            }
        });
    }

}
