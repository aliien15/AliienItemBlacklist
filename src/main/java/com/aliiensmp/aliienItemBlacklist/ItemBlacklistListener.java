package com.aliiensmp.aliienItemBlacklist;

import com.aliiensmp.aliienItemBlacklist.utils.ItemsCache;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class ItemBlacklistListener implements Listener {
    private final ItemsCache cache;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ItemBlacklistListener(ItemsCache cache) {
        this.cache = cache;
    }

    private boolean isBlacklisted(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;
        return cache.isBlacklisted(item.getType());
    }

    private void sendAlert(Player player) {
        if (!cache.isShowAlerts()) return;

        String alertMsg = cache.getAlertMsg();
        if (alertMsg == null || alertMsg.isEmpty()) return;

        String finalMsg = alertMsg.replace("%player%", player.getName());

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("aliien.itemblacklist.alert")) {
                onlinePlayer.sendMessage(mm.deserialize(finalMsg));
            }
        }

        Bukkit.getConsoleSender().sendMessage(mm.deserialize(finalMsg));
    }

    @EventHandler
    public void onPlayerPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (!cache.isStrictMode() && player.hasPermission("aliien.itemblacklist.bypass")) return;

        if (isBlacklisted(event.getItem().getItemStack())) {
            event.setCancelled(true);
            event.getItem().remove();
            sendAlert(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerClickInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!cache.isStrictMode() && player.hasPermission("aliien.itemblacklist.bypass")) return;

        if (isBlacklisted(event.getCurrentItem())) {
            event.setCancelled(true);
            event.getCurrentItem().setAmount(0);
            sendAlert(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void stopShiftDragging(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!cache.isStrictMode() && player.hasPermission("aliien.itemblacklist.bypass")) return;

        if (isBlacklisted(event.getOldCursor())) {
            event.setCancelled(true);
            event.setCursor(null);
            sendAlert(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void blockIllegalCrafting(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!cache.isStrictMode() && player.hasPermission("aliien.itemblacklist.bypass")) return;

        if (isBlacklisted(event.getCurrentItem())) {
            event.setCancelled(true);
            sendAlert(player);
        }
    }
}