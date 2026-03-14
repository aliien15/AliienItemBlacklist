package com.aliiensmp.aliienItemBlacklist;

import com.aliiensmp.aliienItemBlacklist.utils.ItemsCache;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemBlacklistCommand implements CommandExecutor, TabCompleter {
    private final AliienItemBlacklist plugin;
    private final ItemsCache cache;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ItemBlacklistCommand(AliienItemBlacklist plugin, ItemsCache cache) {
        this.plugin = plugin;
        this.cache = cache;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) return true;

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("aliien.itemblacklist.reload")) {
                sender.sendMessage(mm.deserialize(cache.getNoPermMsg()));
                return true;
            }

            // Reload the config, THEN reload the cache!
            plugin.reloadConfig();
            cache.loadCache();

            sender.sendMessage(mm.deserialize(cache.getReloadMsg()));
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            List<String> subcommands = List.of("reload");
            List<String> completions = new ArrayList<>();

            for (String sub : subcommands) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
            return completions;
        }
        return List.of();
    }
}