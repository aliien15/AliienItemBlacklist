package com.aliiensmp.aliienItemBlacklist;

import com.aliiensmp.aliienItemBlacklist.utils.ItemsCache;
import com.aliiensmp.aliienItemBlacklist.utils.UpdateChecker;
import com.aliiensmp.aliienItemBlacklist.utils.UpdateNotifyListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class AliienItemBlacklist extends JavaPlugin {

    private ItemsCache cache;

    private final String updateGistUrl = "https://gist.githubusercontent.com/aliien15/acc305f002bd258e169b9316a96aca26/raw/AliienItemBlacklist-version.txt";

    @Override
    public void onEnable() {
        saveDefaultConfig();

        cache = new ItemsCache(this);
        cache.loadCache();

        updateChecker();

        getServer().getPluginManager().registerEvents(new ItemBlacklistListener(this, cache), this);
        getServer().getPluginManager().registerEvents(new UpdateNotifyListener(this, cache, updateGistUrl), this);
        Objects.requireNonNull(getCommand("itemblacklist")).setExecutor(new ItemBlacklistCommand(this, cache));

        getLogger().info("AliienItemBlacklist is enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AliienItemBlacklist is disabled!");
    }

    private void updateChecker() {
        new UpdateChecker(this, updateGistUrl).getVersion(version -> {
            if (this.getPluginMeta().getVersion().equals(version)) {
                getLogger().info("AliienItemBlacklist is up to date!");
            } else {
                getLogger().warning("A new update is available for AliienItemBlacklist!");
            }
        });
    }
}