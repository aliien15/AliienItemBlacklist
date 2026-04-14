package com.aliiensmp.aliienItemBlacklist;

import com.aliiensmp.aliienItemBlacklist.utils.ItemsCache;
import com.aliiensmp.core.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class ItemBlacklistListener implements Listener {
    private final ItemsCache cache;
    private final AliienItemBlacklist plugin;

    public ItemBlacklistListener(AliienItemBlacklist plugin, ItemsCache cache) {
        this.plugin = plugin;
        this.cache = cache;
    }

    private void logBlacklistedItem(Player player, Material mat) {
        if (!cache.isEnableLogging()) return;

        CompletableFuture.runAsync(() -> {
            try {
                File dataFolder = plugin.getDataFolder();
                if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                    plugin.getLogger().warning("Could not create plugin data folder for logging!");
                    return;
                }

                File logFile = new File(dataFolder, "logs.txt");
                if (!logFile.exists() && !logFile.createNewFile()) {
                    plugin.getLogger().warning("Could not create logs.txt file!");
                    return;
                }

                String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
                String logMessage = "[" + timestamp + "] Player " + player.getName() + " had a " + mat.name() + " item.";

                try (FileWriter fw = new FileWriter(logFile, true);
                     PrintWriter pw = new PrintWriter(fw)) {
                    pw.println(logMessage);
                }
            } catch (IOException e) {
                plugin.getLogger().log(java.util.logging.Level.SEVERE, "An error occurred while writing to logs.txt!", e);
            }
        });
    }

    private boolean isBlacklisted(ItemStack item) {
        return item != null && !item.getType().isAir() && cache.isBlacklisted(item.getType());
    }

    /**
     * Handle global and per-item permissions
     */
    private boolean hasBypass(Player player, Material material) {
        if (cache.isStrictMode()) return false;
        if (player.hasPermission("aliien.itemblacklist.bypass")) return true;

        return player.hasPermission("aliien.itemblacklist.bypass." + material.name().toLowerCase());
    }

    private void sendAlert(Player player, Material mat) {
        logBlacklistedItem(player, mat);
        if (!cache.isShowAlerts()) return;

        String prefix = cache.getPrefix();
        String msg = cache.getAlertMsg();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("aliien.itemblacklist.alert")) {
                MessageUtils.send(onlinePlayer, prefix, msg, "%player%", player.getName(), "%item%", mat.name());
                cache.playAlert(onlinePlayer);
            }
        }

        MessageUtils.send(Bukkit.getConsoleSender(), prefix, msg, "%player%", player.getName(), "%item%", mat.name());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (cache.isWorldDisabled(player.getWorld().getName())) return;

        ItemStack item = event.getItem().getItemStack();

        if (!isBlacklisted(item)) return;
        if (hasBypass(player, item.getType())) return;

        Material savedBadMat = item.getType();
        event.setCancelled(true);
        event.getItem().remove();
        sendAlert(player, savedBadMat);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerClickInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (cache.isWorldDisabled(player.getWorld().getName())) return;

        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        boolean badCurrent = isBlacklisted(currentItem);
        boolean badCursor = isBlacklisted(cursorItem);

        if (!badCurrent && !badCursor) return;

        boolean blockCurrent = badCurrent && !hasBypass(player, currentItem.getType());
        boolean blockCursor = badCursor && !hasBypass(player, cursorItem.getType());
        if (!blockCurrent && !blockCursor) return;

        if (blockCurrent) {
            Material savedBadMat = currentItem.getType();
            event.setCancelled(true);
            event.setCurrentItem(null);
            sendAlert(player, savedBadMat);
        }

        if (blockCursor) {
            Material savedBadMat = cursorItem.getType();
            event.setCancelled(true);
            event.getView().setCursor(null);
            sendAlert(player, savedBadMat);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void stopShiftDragging(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (cache.isWorldDisabled(player.getWorld().getName())) return;

        ItemStack oldCursor = event.getOldCursor();

        if (!isBlacklisted(oldCursor)) return;
        if (hasBypass(player, oldCursor.getType())) return;

        Material savedBadMat = oldCursor.getType();
        event.setCancelled(true);
        event.getView().setCursor(null);
        sendAlert(player, savedBadMat);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (!(event.getView().getPlayer() instanceof Player player)) return;

        if (cache.isWorldDisabled(player.getWorld().getName())) return;

        ItemStack result = event.getInventory().getResult();

        if (!isBlacklisted(result)) return;
        if (hasBypass(player, result.getType())) return;

        event.getInventory().setResult(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (cache.isWorldDisabled(player.getWorld().getName())) return;

        ItemStack droppedItem = event.getItemDrop().getItemStack();

        if (!isBlacklisted(droppedItem)) return;
        if (hasBypass(player, droppedItem.getType())) return;

        Material savedBadMat = droppedItem.getType();
        event.getItemDrop().remove();
        sendAlert(player, savedBadMat);
    }
}