package com.aliiensmp.aliienItemBlacklist.commands;

import com.aliiensmp.aliienItemBlacklist.AliienItemBlacklist;
import com.aliiensmp.aliienItemBlacklist.gui.MainMenuGUI;
import com.aliiensmp.aliienItemBlacklist.utils.ItemsCache;
import com.aliiensmp.core.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Commands implements CommandExecutor {
    private final AliienItemBlacklist plugin;
    private final ItemsCache cache;

    public Commands(AliienItemBlacklist plugin, ItemsCache cache) {
        this.plugin = plugin;
        this.cache = cache;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = cache.getPrefix();

        // Open the main menu
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                MessageUtils.send(sender, prefix, "Only players can open the GUI!");
                return true;
            }

            if (!player.hasPermission("aliien.itemblacklist.edit")) {
                MessageUtils.send(player, prefix, cache.getNoPermMsg());
                return true;
            }

            MainMenuGUI.open(player, plugin, cache);
            return true;
        }

        String action = args[0].toLowerCase();

        if (action.equals("reload")) {
            if (!sender.hasPermission("aliien.itemblacklist.reload")) {
                MessageUtils.send(sender, prefix, cache.getNoPermMsg());
                return true;
            }

            try {
                plugin.getCustomConfig().reload();
                plugin.getMessages().reload();
                plugin.getSettings().reload();
                cache.loadCache();

                MessageUtils.send(sender, prefix, cache.getReloadMsg());
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to reload configuration files!");
                MessageUtils.send(sender, prefix, cache.getFailReloadMsg());
            }
            return true;
        }

        return true;
    }
}