package com.aliiensmp.aliienItemBlacklist.utils;

import com.aliiensmp.aliienItemBlacklist.AliienItemBlacklist;
import com.aliiensmp.core.utils.sounds.CustomSound;
import com.aliiensmp.core.utils.sounds.SoundUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

public class ItemsCache {

    private final AliienItemBlacklist plugin;

    private final YamlDocument config;
    private final YamlDocument messages;
    private final YamlDocument settings;

    private final Set<Material> blacklistedItems = EnumSet.noneOf(Material.class);
    private final Set<String> disabledWorlds = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    private boolean showAlerts;
    private boolean strictMode;
    private boolean enableLogging;
    private boolean checkForUpdates;
    private boolean soundsEnabled;

    private CustomSound clickSound;
    private CustomSound successSound;
    private CustomSound errorSound;
    private CustomSound alertSound;

    private String prefix;
    private String alertMsg;
    private String noPermMsg;
    private String reloadMsg;
    private String failReloadMsg;
    private String notHoldingItemMsg;
    private String addedItemToBlacklistMsg;
    private String itemAlreadyBlacklistedMsg;
    private String newVersionMsg;

    public ItemsCache(AliienItemBlacklist plugin, YamlDocument config, YamlDocument messages, YamlDocument settings) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.settings = settings;
    }

    public void loadCache() {
        blacklistedItems.clear();

        // Load Settings
        showAlerts = settings.getBoolean("show-alerts", true);
        strictMode = settings.getBoolean("strict-mode", false);
        enableLogging = settings.getBoolean("enable-logging", true);
        checkForUpdates = settings.getBoolean("check-for-updates", true);
        soundsEnabled = settings.getBoolean("sounds.enabled", true);

        // Load Sounds
        clickSound = SoundUtils.parse(settings.getString("sounds.click", "none"));
        successSound = SoundUtils.parse(settings.getString("sounds.success", "none"));
        errorSound = SoundUtils.parse(settings.getString("sounds.error", "none"));
        alertSound = SoundUtils.parse(settings.getString("sounds.alert", "none"));

        // Cache the materials
        List<String> items = config.getStringList("blacklisted-items");
        for (String itemName : items) {
            Material mat = Material.matchMaterial(itemName.toUpperCase());
            if (mat != null) {
                blacklistedItems.add(mat);
            } else {
                plugin.getLogger().warning("Invalid material in config: " + itemName);
            }
        }

        // Cache disabled worlds
        disabledWorlds.clear();
        disabledWorlds.addAll(settings.getStringList("disabled-worlds"));

        // Cache the messages
        prefix = messages.getString("prefix", "<dark_red><bold>PUNISH</bold></dark_red> <dark_gray>»</dark_gray> ");
        alertMsg = messages.getString("alert", "<red><bold>THE PLAYER %player% HAD A BLACKLISTED ITEM! (%item%)");
        noPermMsg = messages.getString("no-permission", "<red>You do not have permission to perform this command!");
        reloadMsg = messages.getString("reload", "<green>AliienItemBlacklist has been reloaded successfully!");
        failReloadMsg = messages.getString("fail-reload", "<red>There was an error while reloading AliienItemBlacklist!");
        notHoldingItemMsg = messages.getString("not-holding-item", "<red>You must be holding an item to do this!");
        addedItemToBlacklistMsg = messages.getString("added-item-to-blacklist", "<green>The item %item% has successfully been added to the blacklist!");
        itemAlreadyBlacklistedMsg = messages.getString("item-already-blacklisted", "<red>The item %item% is already blacklisted!");
        newVersionMsg = messages.getString("new-version", "<green>A new AliienItemBlacklist version is now available!");

        plugin.getLogger().info("Successfully loaded " + blacklistedItems.size() + " blacklisted items into memory!");
    }

    public boolean isBlacklisted(Material material) {
        return blacklistedItems.contains(material);
    }

    public Set<Material> getBlacklistedItems() {
        return blacklistedItems;
    }

    public void playClick(Player player) {
        if (soundsEnabled && clickSound != null) clickSound.play(player);
    }

    public void playSuccess(Player player) {
        if (soundsEnabled && successSound != null) successSound.play(player);
    }

    public void playError(Player player) {
        if (soundsEnabled && errorSound != null) errorSound.play(player);
    }

    public void playAlert(Player player) {
        if (soundsEnabled && alertSound != null) alertSound.play(player);
    }

    /**
     * @requires material is not air && material has a valid name
     */
    public boolean addBlacklistedItem(Material mat) {
        if (blacklistedItems.contains(mat)) return false;

        blacklistedItems.add(mat);
        config.set("blacklisted-items", blacklistedItems.stream().map(Material::name).toList());
        saveConfigAsync();
        return true;
    }

    /**
     * @requires material is not air && material has a valid name
     */
    public boolean removeBlacklistedItem(Material mat) {
        if (!blacklistedItems.contains(mat)) return false;

        blacklistedItems.remove(mat);
        config.set("blacklisted-items", blacklistedItems.stream().map(Material::name).toList());
        saveConfigAsync();
        return true;
    }

    public void toggleAlerts() {
        showAlerts = !showAlerts;
        settings.set("show-alerts", showAlerts);
        saveSettingsAsync();
    }

    public void toggleStrictMode() {
        strictMode = !strictMode;
        settings.set("strict-mode", strictMode);
        saveSettingsAsync();
    }

    public void toggleEnableLogging() {
        enableLogging = !enableLogging;
        settings.set("enable-logging", enableLogging);
        saveSettingsAsync();
    }

    public void toggleCheckForUpdates() {
        checkForUpdates = !checkForUpdates;
        settings.set("check-for-updates", checkForUpdates);
        saveSettingsAsync();
    }

    public void toggleSoundsEnabled() {
        soundsEnabled = !soundsEnabled;
        settings.set("sounds.enabled", soundsEnabled);
        saveSettingsAsync();
    }

    private void saveSettingsAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                settings.save();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to save settings.yml: " + e.getMessage());
            }
        });
    }

    private void saveConfigAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                config.save();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to save config.yml: " + e.getMessage());
            }
        });
    }

    public boolean isShowAlerts() { return showAlerts; }
    public boolean isStrictMode() { return strictMode; }
    public boolean isEnableLogging() { return enableLogging; }
    public boolean isCheckForUpdates() { return checkForUpdates; }
    public boolean isSoundsEnabled() { return soundsEnabled; }
    public boolean isWorldDisabled(String worldName) { return disabledWorlds.contains(worldName); }

    // This stuff is not being used anywhere, but keeping here just in case
    // public CustomSound getClickSound() { return clickSound; }
    // public CustomSound getSuccessSound() { return successSound; }
    // public CustomSound getErrorSound() { return errorSound; }
    // public CustomSound getAlertSound() { return alertSound; }

    public String getPrefix() { return prefix; }
    public String getAlertMsg() { return alertMsg; }
    public String getNoPermMsg() { return noPermMsg; }
    public String getReloadMsg() { return reloadMsg; }
    public String getFailReloadMsg() { return failReloadMsg; }
    public String getNotHoldingItemMsg() { return notHoldingItemMsg; }
    public String getAddedItemToBlacklistMsg() { return  addedItemToBlacklistMsg; }
    public String getItemAlreadyBlacklistedMsg() { return itemAlreadyBlacklistedMsg; }
    public String getNewVersionMsg() { return newVersionMsg; }
}