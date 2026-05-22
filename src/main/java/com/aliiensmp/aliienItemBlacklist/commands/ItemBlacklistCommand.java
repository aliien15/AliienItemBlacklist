package com.aliiensmp.aliienItemBlacklist.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.aliiensmp.aliienItemBlacklist.AliienItemBlacklist;
import com.aliiensmp.aliienItemBlacklist.config.Messages;
import com.aliiensmp.aliienItemBlacklist.config.Settings;
import com.aliiensmp.aliienItemBlacklist.gui.MainMenuGUI;
import com.aliiensmp.aliienItemBlacklist.utils.ItemsCache;
import com.aliiensmp.core.config.ConfigManager;
import com.aliiensmp.core.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

@CommandAlias("itemblacklist")
public class ItemBlacklistCommand extends BaseCommand {

    private final AliienItemBlacklist plugin;
    private final ItemsCache cache;

    public ItemBlacklistCommand(AliienItemBlacklist plugin, ItemsCache cache) {
        this.plugin = plugin;
        this.cache = cache;
    }

    @Default
    @CommandPermission("aliien.itemblacklist.edit")
    public void openMenu(Player player) {
        MainMenuGUI.open(player, plugin, cache);
    }

    @Subcommand("reload")
    @CommandPermission("aliien.itemblacklist.reload")
    public void onReload(CommandSender sender) {
        CompletableFuture.runAsync(() -> {
            boolean success = true;
            try {
                plugin.getCustomConfig().reload();
                plugin.getMessages().reload();
                plugin.getSettings().reload();

                ConfigManager.bindConfig(plugin.getMessages(), Messages.class);
                ConfigManager.bindConfig(plugin.getSettings(), Settings.class);

                cache.loadCache();
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to reload configuration files: " + e.getMessage());
                success = false;
            }

            boolean finalSuccess = success;
            Runnable task = () -> {
                if (finalSuccess) {
                    MessageUtils.send(sender, Messages.PREFIX, Messages.RELOAD);
                } else {
                    MessageUtils.send(sender, Messages.PREFIX, Messages.FAIL_RELOAD);
                }
            };

            if (sender instanceof Player p) {
                p.getScheduler().run(plugin, scheduledTask -> task.run(), null);
            } else {
                plugin.getServer().getGlobalRegionScheduler().run(plugin, scheduledTask -> task.run());
            }
        });
    }
}