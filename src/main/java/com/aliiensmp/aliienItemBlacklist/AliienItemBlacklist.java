package com.aliiensmp.aliienItemBlacklist;

import com.aliiensmp.aliienItemBlacklist.commands.AutoTabCompleter;
import com.aliiensmp.aliienItemBlacklist.commands.Commands;
import com.aliiensmp.aliienItemBlacklist.utils.ItemsCache;
import com.aliiensmp.core.AliienCore;
import com.aliiensmp.core.bstats.bukkit.Metrics;
import com.aliiensmp.core.bstats.charts.SimplePie;
import com.aliiensmp.core.config.ConfigManager;
import com.aliiensmp.core.utils.ColorUtils;
import com.aliiensmp.core.utils.updatechecker.UpdateChecker;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Objects;

public final class AliienItemBlacklist extends JavaPlugin {

    private YamlDocument config;
    private YamlDocument messages;
    private YamlDocument settings;

    private ItemsCache cache;

    private final String updateGistUrl = "https://gist.githubusercontent.com/aliien15/acc305f002bd258e169b9316a96aca26/raw/AliienItemBlacklist-version.txt";

    @Override
    public void onEnable() {
        AliienCore.init(this);

        if (!loadConfigurations()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        cache = new ItemsCache(this, config, messages, settings);
        cache.loadCache();

        getServer().getPluginManager().registerEvents(new ItemBlacklistListener(this, cache), this);

        registerCommands();

        setupUpdateChecker();

        setupBStats();

        getLogger().info("AliienItemBlacklist is enabled!");
    }

    private void setupBStats() {
        Metrics metrics = AliienCore.enableMetrics(this, 30662);

        metrics.addCustomChart(new SimplePie("strict_mode_status", () -> {
            return cache.isStrictMode() ? "Enabled" : "Disabled";
        }));

        metrics.addCustomChart(new SimplePie("logging_status", () -> {
            return cache.isEnableLogging() ? "Enabled" : "Disabled";
        }));
    }

    @Override
    public void onDisable() {
        getLogger().info("AliienItemBlacklist is disabled!");
    }

    private boolean loadConfigurations() {
        try {
            config = ConfigManager.loadConfig(this, "config.yml");
            messages = ConfigManager.loadConfig(this, "messages.yml");
            settings = ConfigManager.loadConfig(this, "settings.yml");
            return true;
        } catch (IOException e) {
            getLogger().log(java.util.logging.Level.SEVERE, "Failed to load or update configuration files!", e);
            return false;
        }
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("itemblacklist")).setExecutor(new Commands(this, cache));
        Objects.requireNonNull(getCommand("itemblacklist")).setTabCompleter(new AutoTabCompleter());
    }

    private void setupUpdateChecker() {
        if (!settings.getBoolean("check-for-updates", true)) return;

        new UpdateChecker(this, updateGistUrl).getVersion(version -> {
            if (!this.getPluginMeta().getVersion().equals(version)) {
                getLogger().warning("A new update is available for AliienItemBlacklist!");
            }
        });

        getServer().getPluginManager().registerEvents(
                new com.aliiensmp.core.utils.updatechecker.UpdateNotifyListener(
                        this,
                        updateGistUrl,
                        "aliien.itemblacklist.version-notify",
                        () -> ColorUtils.color(cache.getNewVersionMsg())
                ),
                this
        );
    }

    public YamlDocument getCustomConfig() { return config; }
    public YamlDocument getMessages() { return messages; }
    public YamlDocument getSettings() { return settings; }
}