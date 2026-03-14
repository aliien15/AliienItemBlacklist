package com.aliiensmp.aliienItemBlacklist;

import com.aliiensmp.aliienItemBlacklist.utils.ItemsCache;
import com.aliiensmp.aliienItemBlacklist.utils.UpdateChecker;
import com.aliiensmp.aliienItemBlacklist.utils.UpdateNotifyListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class AliienItemBlacklist extends JavaPlugin {

    private ItemsCache cache;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        cache = new ItemsCache(this);
        cache.loadCache();

        String gistUrl = "https://gist.githubusercontent.com/aliien15/acc305f002bd258e169b9316a96aca26/raw/f1dbf6d22d568b4d0fb24c0a779c786788418fc6/AliienItemBlacklist-version.txt";
        updateChecker(gistUrl);

        getServer().getPluginManager().registerEvents(new ItemBlacklistListener(cache), this);
        getServer().getPluginManager().registerEvents(new UpdateNotifyListener(this, cache, gistUrl), this);
        Objects.requireNonNull(getCommand("itemblacklist")).setExecutor(new ItemBlacklistCommand(this, cache));

        getLogger().info("AliienItemBlacklist is enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AliienItemBlacklist is disabled!");
    }

    private void updateChecker(String gistUrl) {
        new UpdateChecker(this, gistUrl).getVersion(version -> {
            String currentVersion = this.getPluginMeta().getVersion();

            if (currentVersion.equals(version)) {
                getLogger().info("AliienItemBlacklist is up to date!");
            } else {
                getLogger().warning("A new update is available for AliienItemBlacklist!");
            }
        });
    }
}