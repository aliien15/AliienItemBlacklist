package com.aliiensmp.aliienItemBlacklist;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class AliienItemBlacklist extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new ItemBlacklistListener(this), this);
        Objects.requireNonNull(getCommand("itemblacklist")).setExecutor(new ItemBlacklistCommand(this));

        getLogger().info("AliienItemBlacklist is enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AliienItemBlacklist is disabled!");
    }
}
