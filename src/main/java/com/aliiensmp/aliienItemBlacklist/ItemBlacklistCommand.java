package com.aliiensmp.aliienItemBlacklist;

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
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ItemBlacklistCommand(AliienItemBlacklist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) return true;

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("aliien.itemblacklist.reload")) {
                String noPermMsg = plugin.getConfig().getString("no-permission", "<red>You do not have permission to perform this command!");
                sender.sendMessage(mm.deserialize(noPermMsg));
                return true;
            }
            plugin.reloadConfig();

            String reloadMsg = plugin.getConfig().getString("reload", "<green>AliienItemBlacklist reloaded successfully!");
            sender.sendMessage(mm.deserialize(reloadMsg));
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        if (args.length == 1) {
            // Auto tab before typing anything
            List<String> subcommands = List.of("reload");
            List<String> completions = new ArrayList<>();

            // Filter based on what they already typed
            for (String sub : subcommands) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
            return completions;
        }

        // Empty list so it doesn't suggest anything else other than the essential
        return List.of();
    }
}
