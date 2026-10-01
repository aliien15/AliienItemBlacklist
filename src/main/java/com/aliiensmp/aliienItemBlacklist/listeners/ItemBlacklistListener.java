package com.aliiensmp.aliienItemBlacklist.listeners;

import com.aliiensmp.aliienItemBlacklist.AliienItemBlacklist;
import com.aliiensmp.aliienItemBlacklist.config.Messages;
import com.aliiensmp.aliienItemBlacklist.config.Settings;
import com.aliiensmp.aliienItemBlacklist.services.AlertLogger;
import com.aliiensmp.aliienItemBlacklist.services.DiscordLogger;
import com.aliiensmp.aliienItemBlacklist.utils.ItemsCache;
import com.aliiensmp.core.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class ItemBlacklistListener implements Listener {
    private final ItemsCache cache;
    private final AliienItemBlacklist plugin;
    private final AlertLogger alertLogger;

    public ItemBlacklistListener(AliienItemBlacklist plugin, ItemsCache cache) {
        this.plugin = plugin;
        this.cache = cache;
        this.alertLogger = plugin.getAlertLogger();
    }

    private boolean isBlacklisted(ItemStack item) {
        return item != null && !item.getType().isAir() && cache.isBlacklisted(item.getType());
    }

    private boolean hasBypass(Player player, Material material) {
        if (Settings.STRICT_MODE) return false;

        return player.hasPermission("aliien.itemblacklist.bypass")
                || player.hasPermission("aliien.itemblacklist.bypass." + material.name().toLowerCase(Locale.ROOT));
    }

    private void sendAlert(Player player, ItemStack item, String event) {
        alertLogger.logBlacklistedItem(player, item.getType());
        DiscordLogger.sendEmbed(player, item, event);
        if (!Settings.SHOW_ALERTS) return;

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(onlinePlayer -> onlinePlayer.hasPermission("aliien.itemblacklist.alert"))
                .forEach(onlinePlayer -> {
                    MessageUtils.send(onlinePlayer, Messages.PREFIX, Messages.ALERT, "%player%", player.getName(), "%item%", item.getType().name());
                    cache.playAlert(onlinePlayer);
                });

        MessageUtils.send(Bukkit.getConsoleSender(), Messages.PREFIX, Messages.ALERT, "%player%", player.getName(), "%item%", item.getType().name());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (cache.isWorldDisabled(player.getWorld().getName())) return;

        boolean confiscated = false;
        ItemStack lastBadItem = null;

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (isBlacklisted(item) && !hasBypass(player, item.getType())) {
                lastBadItem = item;
                player.getInventory().setItem(i, null);
                confiscated = true;
            }
        }

        if (confiscated) {
            sendAlert(player, lastBadItem, "Player join");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onOffhandSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (cache.isWorldDisabled(player.getWorld().getName())) return;

        ItemStack mainHand = event.getMainHandItem();
        ItemStack offHand = event.getOffHandItem();

        boolean badMain = isBlacklisted(mainHand) && !hasBypass(player, mainHand.getType());
        boolean badOff = isBlacklisted(offHand) && !hasBypass(player, offHand.getType());

        if (badMain) {
            event.setCancelled(true);
            player.getInventory().setItemInOffHand(null);
            sendAlert(player, mainHand, "Item swap to main hand");
        } else if (badOff) {
            event.setCancelled(true);
            player.getInventory().setItemInMainHand(null);
            sendAlert(player, offHand, "Item swap to off hand");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (cache.isWorldDisabled(player.getWorld().getName())) return;

        ItemStack item = event.getItem().getItemStack();

        if (!isBlacklisted(item)) return;
        if (hasBypass(player, item.getType())) return;

        event.setCancelled(true);
        event.getItem().remove();
        sendAlert(player, item, "Pickup item");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerClickInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (cache.isWorldDisabled(player.getWorld().getName())) return;

        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        if (event.getClick() == ClickType.NUMBER_KEY) {
            ItemStack hotbarItem = player.getInventory().getItem(event.getHotbarButton());
            if (isBlacklisted(hotbarItem) && !hasBypass(player, hotbarItem.getType())) {
                event.setCancelled(true);

                player.getInventory().setItem(event.getHotbarButton(), null);

                String invType = event.getClickedInventory() != null ? event.getClickedInventory().getType().name() : "OUTSIDE";
                sendAlert(player, hotbarItem, "Item click on inventory: " + invType);

                return;
            }
        }

        boolean badCurrent = isBlacklisted(currentItem);
        boolean badCursor = isBlacklisted(cursorItem);

        if (!badCurrent && !badCursor) return;

        boolean blockCurrent = badCurrent && !hasBypass(player, currentItem.getType());
        boolean blockCursor = badCursor && !hasBypass(player, cursorItem.getType());

        if (!blockCurrent && !blockCursor) return;

        if (blockCurrent) {

            event.setCancelled(true);
            event.setCurrentItem(null);

            String invType = event.getClickedInventory() != null ? event.getClickedInventory().getType().name() : "OUTSIDE";
            sendAlert(player, currentItem, "Item click on inventory: " + invType);
        }

        if (blockCursor) {
            event.setCancelled(true);
            event.getView().setCursor(null);

            String invType = event.getClickedInventory() != null ? event.getClickedInventory().getType().name() : "OUTSIDE";
            sendAlert(player, cursorItem, "Item click on inventory: " + invType);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void stopShiftDragging(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (cache.isWorldDisabled(player.getWorld().getName())) return;

        ItemStack oldCursor = event.getOldCursor();

        if (!isBlacklisted(oldCursor)) return;
        if (hasBypass(player, oldCursor.getType())) return;

        event.setCancelled(true);
        event.getView().setCursor(null);

        sendAlert(player, oldCursor, "Item shift-click on inventory: " + event.getInventory().getType().name());
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

        event.getItemDrop().remove();
        sendAlert(player, droppedItem, "Drop item");
    }
}