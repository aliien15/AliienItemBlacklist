package com.aliiensmp.aliienItemBlacklist;

import com.aliiensmp.aliienItemBlacklist.UpdateChecker;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateNotifyListener implements Listener {
    private final AliienItemBlacklist plugin;
    private final String gistUrl;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public UpdateNotifyListener(AliienItemBlacklist plugin, String gistUrl) {
        this.plugin = plugin;
        this.gistUrl = gistUrl;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Only notify players with the admin permission
        if (player.hasPermission("aliien.chatcolor.admin")) {
            new UpdateChecker(plugin, gistUrl).getVersion(version -> {
                // Check if the server's version matches the Gist's version
                if (!plugin.getPluginMeta().getVersion().equals(version)) {
                    String updateMsg = plugin.getConfig().getString("new-version", "<green>A new AliienItemBlacklist version is now available!");
                    player.sendMessage(mm.deserialize(updateMsg));
                }
            });
        }
    }
}
