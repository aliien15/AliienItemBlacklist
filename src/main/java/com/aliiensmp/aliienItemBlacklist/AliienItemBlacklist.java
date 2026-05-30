package com.aliiensmp.aliienItemBlacklist;

import co.aikar.commands.MessageKeys;
import co.aikar.commands.PaperCommandManager;
import com.aliiensmp.aliienItemBlacklist.commands.ItemBlacklistCommand;
import com.aliiensmp.aliienItemBlacklist.config.Messages;
import com.aliiensmp.aliienItemBlacklist.config.Settings;
import com.aliiensmp.aliienItemBlacklist.listeners.ItemBlacklistListener;
import com.aliiensmp.aliienItemBlacklist.services.AlertLogger;
import com.aliiensmp.aliienItemBlacklist.utils.ItemsCache;
import com.aliiensmp.core.AliienCore;
import com.aliiensmp.core.config.ConfigManager;
import com.aliiensmp.core.utils.ColorUtils;
import com.aliiensmp.core.utils.updatechecker.UpdateChecker;
import com.aliiensmp.core.utils.updatechecker.UpdateNotifyListener;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.zapper.DependencyManager;
import revxrsal.zapper.classloader.URLClassLoaderWrapper;
import revxrsal.zapper.relocation.Relocation;
import revxrsal.zapper.repository.Repository;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.logging.Level;

public final class AliienItemBlacklist extends JavaPlugin {

    private YamlDocument config;
    private YamlDocument messages;
    private YamlDocument settings;

    private ItemsCache cache;
    private AlertLogger alertLogger;

    private final String updateGistUrl = "https://gist.githubusercontent.com/aliien15/acc305f002bd258e169b9316a96aca26/raw/AliienItemBlacklist-version.txt";

    @Override
    public void onLoad() {
        File librariesFolder = new File(getDataFolder().getParentFile(), "AliienCore");

        DependencyManager dependencyManager = new DependencyManager(
                librariesFolder,
                URLClassLoaderWrapper.wrap((URLClassLoader) getClassLoader())
        );

        dependencyManager.repository(Repository.mavenCentral());
        dependencyManager.repository(Repository.maven("https://jitpack.io"));

        dependencyManager.dependency("com.zaxxer:HikariCP:5.1.0");
        dependencyManager.dependency("com.mysql:mysql-connector-j:9.6.0");
        dependencyManager.dependency("org.xerial:sqlite-jdbc:3.45.1.0");
        dependencyManager.dependency("dev.dejvokep:boosted-yaml:1.3.7");

        dependencyManager.relocate(new Relocation(
                "com{}zaxxer{}hikari".replace("{}", "."),
                "com.aliiensmp.core.lib.hikari"
        ));

        dependencyManager.load();
    }

    @Override
    public void onEnable() {
        AliienCore.init(this);

        if (!loadConfigurations()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        ConfigManager.bindConfig(messages, Messages.class);
        ConfigManager.bindConfig(settings, Settings.class);

        cache = new ItemsCache(this, config, settings);
        cache.loadCache();

        setupServices();
        getServer().getPluginManager().registerEvents(new ItemBlacklistListener(this, cache), this);

        registerCommands();

        setupUpdateChecker();
        setupBStats();

        getLogger().info("AliienItemBlacklist is enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AliienItemBlacklist is disabled!");
    }

    private void setupServices() {
        alertLogger = new AlertLogger(this);
    }

    private boolean loadConfigurations() {
        try {
            config = ConfigManager.loadConfig(this, "config.yml");
            messages = ConfigManager.loadConfig(this, "messages.yml");
            settings = ConfigManager.loadConfig(this, "settings.yml");
            return true;
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to load or update configuration files!", e);
            return false;
        }
    }

    private void setupBStats() {
        Metrics metrics = new Metrics(this, 30662);

        metrics.addCustomChart(new SimplePie("strict_mode_status", () -> {
            return Settings.STRICT_MODE ? "Enabled" : "Disabled";
        }));

        metrics.addCustomChart(new SimplePie("logging_status", () -> {
            return Settings.ENABLE_LOGGING ? "Enabled" : "Disabled";
        }));
    }

    private void registerCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);

        commandManager.getLocales().addMessage(Locale.ENGLISH, MessageKeys.ERROR_PREFIX, Messages.PREFIX);
        commandManager.getLocales().addMessage(Locale.ENGLISH, MessageKeys.PERMISSION_DENIED, Messages.NO_PERMISSION);

        commandManager.registerCommand(new ItemBlacklistCommand(this, cache));
    }

    private void setupUpdateChecker() {
        if (!Settings.CHECK_FOR_UPDATES) return;

        new UpdateChecker(this, updateGistUrl).getVersion(version -> {
            if (!this.getPluginMeta().getVersion().equals(version)) {
                getLogger().warning("A new update is available for AliienItemBlacklist!");
            }
        });

        getServer().getPluginManager().registerEvents(
                new UpdateNotifyListener(
                        this,
                        updateGistUrl,
                        "aliien.itemblacklist.version-notify",
                        () -> ColorUtils.color(Messages.NEW_VERSION)
                ),
                this
        );
    }

    public YamlDocument getCustomConfig() { return config; }
    public YamlDocument getMessages() { return messages; }
    public YamlDocument getSettings() { return settings; }

    public AlertLogger getAlertLogger() { return alertLogger; }
}