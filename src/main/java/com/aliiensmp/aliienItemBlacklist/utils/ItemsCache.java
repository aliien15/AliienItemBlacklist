package com.aliiensmp.aliienItemBlacklist.utils;

import com.aliiensmp.aliienItemBlacklist.AliienItemBlacklist;
import com.aliiensmp.aliienItemBlacklist.config.Settings;
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
    private final YamlDocument settings;

    private final Set<Material> blacklistedItems = EnumSet.noneOf(Material.class);
    private final Set<String> disabledWorlds = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    private CustomSound clickSound;
    private CustomSound successSound;
    private CustomSound errorSound;
    private CustomSound alertSound;

    public ItemsCache(AliienItemBlacklist plugin, YamlDocument config, YamlDocument settings) {
        this.plugin = plugin;
        this.config = config;
        this.settings = settings;
    }

    public void loadCache() {
        blacklistedItems.clear();
        disabledWorlds.clear();

        // Load Sounds directly from the static Settings variables
        clickSound = SoundUtils.parse(Settings.SOUND_CLICK);
        successSound = SoundUtils.parse(Settings.SOUND_SUCCESS);
        errorSound = SoundUtils.parse(Settings.SOUND_ERROR);
        alertSound = SoundUtils.parse(Settings.SOUND_ALERT);

        if (Settings.DISABLED_WORLDS != null) {
            disabledWorlds.addAll(Settings.DISABLED_WORLDS);
        }

        List<String> items = config.getStringList("blacklisted-items");
        if (items != null) {
            for (String itemName : items) {
                Material mat = Material.matchMaterial(itemName.toUpperCase());
                if (mat != null) {
                    blacklistedItems.add(mat);
                } else {
                    plugin.getLogger().warning("Invalid material in config: " + itemName);
                }
            }
        }

        plugin.getLogger().info("Successfully loaded " + blacklistedItems.size() + " blacklisted items into memory!");
    }

    public boolean isBlacklisted(Material material) {
        return blacklistedItems.contains(material);
    }

    public Set<Material> getBlacklistedItems() {
        return blacklistedItems;
    }

    public void playClick(Player player) {
        if (Settings.SOUNDS_ENABLED && clickSound != null) clickSound.play(player);
    }

    public void playSuccess(Player player) {
        if (Settings.SOUNDS_ENABLED && successSound != null) successSound.play(player);
    }

    public void playError(Player player) {
        if (Settings.SOUNDS_ENABLED && errorSound != null) errorSound.play(player);
    }

    public void playAlert(Player player) {
        if (Settings.SOUNDS_ENABLED && alertSound != null) alertSound.play(player);
    }

    public boolean addBlacklistedItem(Material mat) {
        if (blacklistedItems.contains(mat)) return false;

        blacklistedItems.add(mat);
        config.set("blacklisted-items", blacklistedItems.stream().map(Material::name).toList());
        saveConfigAsync();
        return true;
    }

    public boolean removeBlacklistedItem(Material mat) {
        if (!blacklistedItems.contains(mat)) return false;

        blacklistedItems.remove(mat);
        config.set("blacklisted-items", blacklistedItems.stream().map(Material::name).toList());
        saveConfigAsync();
        return true;
    }

    public void toggleAlerts() {
        Settings.SHOW_ALERTS = !Settings.SHOW_ALERTS;
        settings.set("show-alerts", Settings.SHOW_ALERTS);
        saveSettingsAsync();
    }

    public void toggleStrictMode() {
        Settings.STRICT_MODE = !Settings.STRICT_MODE;
        settings.set("strict-mode", Settings.STRICT_MODE);
        saveSettingsAsync();
    }

    public void toggleEnableLogging() {
        Settings.ENABLE_LOGGING = !Settings.ENABLE_LOGGING;
        settings.set("enable-logging", Settings.ENABLE_LOGGING);
        saveSettingsAsync();
    }

    public void toggleCheckForUpdates() {
        Settings.CHECK_FOR_UPDATES = !Settings.CHECK_FOR_UPDATES;
        settings.set("check-for-updates", Settings.CHECK_FOR_UPDATES);
        saveSettingsAsync();
    }

    public void toggleSoundsEnabled() {
        Settings.SOUNDS_ENABLED = !Settings.SOUNDS_ENABLED;
        settings.set("sounds.enabled", Settings.SOUNDS_ENABLED);
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

    public boolean isWorldDisabled(String worldName) { return disabledWorlds.contains(worldName); }
}