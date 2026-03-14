package com.aliiensmp.aliienItemBlacklist.utils;

import com.aliiensmp.aliienItemBlacklist.AliienItemBlacklist;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateNotifyListener implements Listener {
    private final AliienItemBlacklist plugin;
    private final ItemsCache cache;
    private final String gistUrl;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public UpdateNotifyListener(AliienItemBlacklist plugin, ItemsCache cache, String gistUrl) {
        this.plugin = plugin;
        this.cache = cache;
        this.gistUrl = gistUrl;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("aliien.itemblacklist.version-notify")) {
            new UpdateChecker(plugin, gistUrl).getVersion(version -> {
                if (!plugin.getPluginMeta().getVersion().equals(version)) {
                    player.sendMessage(mm.deserialize(cache.getNewVersionMsg()));
                }
            });
        }
    }
}