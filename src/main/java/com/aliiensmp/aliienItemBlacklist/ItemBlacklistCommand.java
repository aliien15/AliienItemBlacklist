package com.aliiensmp.aliienItemBlacklist;

import com.aliiensmp.aliienItemBlacklist.utils.ItemsCache;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

        String action = args[0].toLowerCase();

        if (action.equals("reload")) {
            if (!sender.hasPermission("aliien.itemblacklist.reload")) {
                sender.sendMessage(mm.deserialize(cache.getNoPermMsg()));
                return true;
            }

            plugin.reloadConfig();
            cache.loadCache();

            sender.sendMessage(mm.deserialize(cache.getReloadMsg()));
            return true;
        }

        if (action.equals("add") || action.equals("remove")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command!");
                return true;
            }

            if (!sender.hasPermission("aliien.itemblacklist.edit")) {
                String noPermMsg = cache.getNoPermMsg();
                sender.sendMessage(mm.deserialize(noPermMsg));
                return true;
            }

            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            if (itemInHand.getType().isAir()) {
                sender.sendMessage(mm.deserialize(cache.getNotHoldingItemMsg()));
                return true;
            }

            String itemName = itemInHand.getType().name();
            switch (action) {
                case "add" -> {
                    if (cache.addBlacklistedItem(itemInHand.getType())) {
                        String msg = cache.getAddedItemToBlacklistMsg().replace("%item%", itemName);
                        player.sendMessage(mm.deserialize(msg));
                    } else {
                        String msg = cache.getItemAlreadyBlacklistedMsg().replace("%item%", itemName);
                        player.sendMessage(mm.deserialize(msg));
                    }
                }
                case "remove" -> {
                    if (cache.removeBlacklistedItem(itemInHand.getType())) {
                        String msg = cache.getRemovedItemFromBlacklistMsg().replace("%item%", itemName);
                        player.sendMessage(mm.deserialize(msg));
                    } else {
                        String msg = cache.getItemNotBlacklistedMsg().replace("%item%", itemName);
                        player.sendMessage(mm.deserialize(msg));
                    }
                }
            }

            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subcommands = List.of("reload", "add", "remove");
            for (String sub : subcommands) {
                if (sub.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
            return completions;
        }

        return List.of();
    }
}