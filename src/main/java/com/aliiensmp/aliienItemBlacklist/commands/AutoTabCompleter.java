package com.aliiensmp.aliienItemBlacklist.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AutoTabCompleter implements TabCompleter {

    private static final List<String> SUBCOMMANDS = List.of("reload");

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return SUBCOMMANDS.stream()
                    .filter(sub -> sub.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }

        return List.of();
    }
}