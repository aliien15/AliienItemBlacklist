package com.aliiensmp.aliienItemBlacklist.services;

import com.aliiensmp.aliienItemBlacklist.AliienItemBlacklist;
import com.aliiensmp.aliienItemBlacklist.config.Settings;
import com.aliiensmp.core.files.FileLogger;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AlertLogger {

    private final AliienItemBlacklist plugin;
    private volatile FileLogger fileLogger;

    public AlertLogger(AliienItemBlacklist plugin) {
        this.plugin = plugin;
        this.fileLogger = new FileLogger(plugin).setFileName("logs.log").setEnabled(Settings.ENABLE_LOGGING);
    }

    /**
     * Logs a blacklist item removal into a .txt file
     *
     * @param player the player caught with a blacklisted item
     * @param mat the blacklisted material that the player was using
     * @requires {@code player != null && mat != null}
     */
    public void logBlacklistedItem(Player player, Material mat) {
        fileLogger.logAsync("Player " + player.getName() + " (" + player.getUniqueId() + ") had a '" + mat.name() + "' item.");
    }

    /**
     * Sets the enabled state of the {@link FileLogger}
     *
     * @param newState new enabled state
     */
    public void toggle(boolean newState) {
        this.fileLogger.setEnabled(newState);
    }
}