package com.aliiensmp.aliienItemBlacklist.utils;

import com.aliiensmp.aliienItemBlacklist.AliienItemBlacklist;
import org.bukkit.Material;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class ItemsCache {

    private final AliienItemBlacklist plugin;
    private final Set<Material> blacklistedItems = EnumSet.noneOf(Material.class);

    private boolean showAlerts;
    private boolean strictMode;
    private boolean enableLogging;

    private String alertMsg;
    private String noPermMsg;
    private String reloadMsg;
    private String notHoldingItemMsg;
    private String addedItemToBlacklistMsg;
    private String itemAlreadyBlacklistedMsg;
    private String removedItemFromBlacklistMsg;
    private String itemNotBlacklistedMsg;
    private String newVersionMsg;

    public ItemsCache(AliienItemBlacklist plugin) {
        this.plugin = plugin;
    }

    public void loadCache() {
        blacklistedItems.clear();

        // Load Settings
        showAlerts = plugin.getConfig().getBoolean("settings.show-alerts", true);
        strictMode = plugin.getConfig().getBoolean("settings.strict-mode", false);
        enableLogging = plugin.getConfig().getBoolean("settings.enable-logging", true);

        // Cache the materials
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
        alertMsg = plugin.getConfig().getString("alert", "<red><bold>THE PLAYER %player% HAD A BLACKLISTED ITEM! (%item%)");
        noPermMsg = plugin.getConfig().getString("no-permission", "<red>You do not have permission to perform this command!");
        reloadMsg = plugin.getConfig().getString("reload", "<green>AliienItemBlacklist has been reloaded successfully!");
        notHoldingItemMsg = plugin.getConfig().getString("not-holding-item", "<red>You must be holding an item to do this!");
        addedItemToBlacklistMsg = plugin.getConfig().getString("added-item-to-blacklist", "<green>The item %item% has successfully been added to the blacklist!");
        itemAlreadyBlacklistedMsg = plugin.getConfig().getString("item-already-blacklisted", "<red>The item %item% is already blacklisted!");
        removedItemFromBlacklistMsg = plugin.getConfig().getString("removed-item-from-blacklist", "<green>Removed %item% from the blacklist!");
        itemNotBlacklistedMsg = plugin.getConfig().getString("item-not-blacklisted", "<red>The item %item% is not blacklisted!");
        newVersionMsg = plugin.getConfig().getString("new-version", "<green>A new AliienItemBlacklist version is now available!");

        plugin.getLogger().info("Successfully loaded " + blacklistedItems.size() + " blacklisted items into memory!");
    }

    public boolean isBlacklisted(Material material) {
        return blacklistedItems.contains(material);
    }

    public Set<Material> getBlacklistedItems() {
        return blacklistedItems;
    }

    /**
     * @requires material is not air && material has a valid name
     */
    public boolean addBlacklistedItem(Material material) {
        if (isBlacklisted(material)) return false;
        blacklistedItems.add(material);

        List<String> list = plugin.getConfig().getStringList("blacklisted-items");
        list.add(material.name());

        plugin.getConfig().set("blacklisted-items", list);
        plugin.saveConfig();
        return true;
    }

    /**
     * @requires material is not air && material has a valid name
     */
    public boolean removeBlacklistedItem(Material material) {
        if (!isBlacklisted(material)) return false;

        blacklistedItems.remove(material);

        List<String> list = plugin.getConfig().getStringList("blacklisted-items");
        list.remove(material.name());

        plugin.getConfig().set("blacklisted-items", list);
        plugin.saveConfig();
        return true;
    }

    public boolean isShowAlerts() { return showAlerts; }
    public boolean isStrictMode() { return strictMode; }
    public boolean isEnableLogging() { return enableLogging; }

    public String getAlertMsg() { return alertMsg; }
    public String getNoPermMsg() { return noPermMsg; }
    public String getReloadMsg() { return reloadMsg; }
    public String getNotHoldingItemMsg() { return notHoldingItemMsg; }
    public String getAddedItemToBlacklistMsg() { return  addedItemToBlacklistMsg; }
    public String getItemAlreadyBlacklistedMsg() { return itemAlreadyBlacklistedMsg; }
    public String getRemovedItemFromBlacklistMsg() { return removedItemFromBlacklistMsg; }
    public String getItemNotBlacklistedMsg() { return itemNotBlacklistedMsg; }
    public String getNewVersionMsg() { return newVersionMsg; }
}