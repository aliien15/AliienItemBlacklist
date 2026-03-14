package com.aliiensmp.aliienItemBlacklist.utils;

import com.aliiensmp.aliienItemBlacklist.AliienItemBlacklist;
import org.bukkit.Material;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemsCache {

    private final AliienItemBlacklist plugin;
    private final Set<Material> blacklistedItems = new HashSet<>();
    private boolean showAlerts;
    private boolean strictMode;
    private String alertMsg;
    private String noPermMsg;
    private String reloadMsg;
    private String newVersionMsg;

    public ItemsCache(AliienItemBlacklist plugin) {
        this.plugin = plugin;
    }

    public void loadCache() {
        blacklistedItems.clear();

        // Load Settings
        showAlerts = plugin.getConfig().getBoolean("settings.show-alerts", true);
        strictMode = plugin.getConfig().getBoolean("settings.strict-mode", false);

        // Cache the materials safely
        List<String> items = plugin.getConfig().getStringList("blacklisted-items");
        for (String itemName : items) {
            Material mat = Material.matchMaterial(itemName.toUpperCase());
            if (mat != null) {
                blacklistedItems.add(mat);
            } else {
                plugin.getLogger().warning("Invalid material in config: " + itemName);
            }
        }

        // Cache the messages
        alertMsg = plugin.getConfig().getString("alert", "");
        noPermMsg = plugin.getConfig().getString("no-permission", "<red>You do not have permission to perform this command!");
        reloadMsg = plugin.getConfig().getString("reload", "<green>AliienItemBlacklist has been reloaded successfully!");
        newVersionMsg = plugin.getConfig().getString("new-version", "<green>A new AliienItemBlacklist version is now available!");

        plugin.getLogger().info("Successfully loaded " + blacklistedItems.size() + " blacklisted items into memory!");
    }

    public boolean isBlacklisted(Material material) {
        return blacklistedItems.contains(material);
    }

    public boolean isShowAlerts() { return showAlerts; }
    public boolean isStrictMode() { return strictMode; }
    public String getAlertMsg() { return alertMsg; }
    public String getNoPermMsg() { return noPermMsg; }
    public String getReloadMsg() { return reloadMsg; }
    public String getNewVersionMsg() { return newVersionMsg; }
}