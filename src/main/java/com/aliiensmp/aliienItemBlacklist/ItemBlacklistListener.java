package com.aliiensmp.aliienItemBlacklist;

import com.aliiensmp.aliienItemBlacklist.utils.ItemsCache;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
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
    private final MiniMessage mm = MiniMessage.miniMessage();
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
        if (item == null || item.getType().isAir()) return false;
        return cache.isBlacklisted(item.getType());
    }

    private void sendAlert(Player player, Material mat) {
        logBlacklistedItem(player, mat);
        if (!cache.isShowAlerts()) return;

        String alertMsg = cache.getAlertMsg().replace("%player%", player.getName()).replace("%item%", mat.name());

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("aliien.itemblacklist.alert")) {
                onlinePlayer.sendMessage(mm.deserialize(alertMsg));
            }
        }

        Bukkit.getConsoleSender().sendMessage(mm.deserialize(alertMsg));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (!cache.isStrictMode() && player.hasPermission("aliien.itemblacklist.bypass")) return;

        if (isBlacklisted(event.getItem().getItemStack())) {
            Material savedBadMat = event.getItem().getItemStack().getType();
            event.setCancelled(true);
            event.getItem().remove();
            sendAlert(player, savedBadMat);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerClickInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!cache.isStrictMode() && player.hasPermission("aliien.itemblacklist.bypass")) return;

        // Item clicking on
        if (isBlacklisted(event.getCurrentItem())) {
            Material savedBadMat = event.getCurrentItem().getType();
            event.setCancelled(true);
            event.getCurrentItem().setAmount(0);
            sendAlert(player, savedBadMat);
        }

        // Item dragged on the cursos
        if (isBlacklisted(event.getCursor())) {
            Material savedBadMat = event.getCursor().getType();
            event.setCancelled(true);
            player.setItemOnCursor(null);
            sendAlert(player, savedBadMat);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void stopShiftDragging(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!cache.isStrictMode() && player.hasPermission("aliien.itemblacklist.bypass")) return;

        if (isBlacklisted(event.getOldCursor())) {
            Material savedBadMat = event.getOldCursor().getType();
            event.setCancelled(true);
            event.setCursor(null);
            sendAlert(player, savedBadMat);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void blockIllegalCrafting(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!cache.isStrictMode() && player.hasPermission("aliien.itemblacklist.bypass")) return;

        if (isBlacklisted(event.getCurrentItem())) {
            Material savedBadMat = event.getCurrentItem().getType();
            event.setCancelled(true);
            sendAlert(player, savedBadMat);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (!cache.isStrictMode() && player.hasPermission("aliien.itemblacklist.bypass")) return;

        ItemStack droppedItem = event.getItemDrop().getItemStack();
        if (isBlacklisted(droppedItem)) {
            Material savedBadMat = droppedItem.getType();
            event.getItemDrop().remove();

            sendAlert(player, savedBadMat);
        }
    }
}