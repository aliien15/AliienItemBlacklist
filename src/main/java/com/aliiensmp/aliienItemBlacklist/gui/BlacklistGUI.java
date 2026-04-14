package com.aliiensmp.aliienItemBlacklist.gui;

import com.aliiensmp.aliienItemBlacklist.AliienItemBlacklist;
import com.aliiensmp.aliienItemBlacklist.utils.ItemsCache;
import com.aliiensmp.core.items.ItemBuilder;
import com.aliiensmp.core.menu.AliienGUI;
import com.aliiensmp.core.menu.ClickableItem;
import com.aliiensmp.core.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.aliiensmp.aliienItemBlacklist.gui.MainMenuGUI.getFiller;

public class BlacklistGUI {

    public static void open(Player player, AliienItemBlacklist plugin, ItemsCache cache, int page) {
        AliienGUI gui = new AliienGUI("<#2b2d31><bold>ItemBlacklist <dark_gray>» <gray>Page %page%", 6);

        List<Material> items = new ArrayList<>(cache.getBlacklistedItems());
        int maxPerPage = 45;

        // Pagination checks
        boolean hasNextPage = items.size() > page * maxPerPage;
        boolean hasPrevPage = page > 1;

        buildBlacklistedItems(gui, player, plugin, cache, page, items, maxPerPage);
        buildNavigationItems(gui, player, plugin, cache, page, hasNextPage, hasPrevPage);

        gui.open(player, page);
    }

    private static void buildBlacklistedItems(AliienGUI gui, Player player, AliienItemBlacklist plugin, ItemsCache cache, int page, List<Material> items, int maxPerPage) {
        int startIndex = (page - 1) * maxPerPage;
        int endIndex = Math.min(startIndex + maxPerPage, items.size());

        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            Material mat = items.get(i);
            ClickableItem cItem = new ItemBuilder(mat)
                    .addLoreLine("")
                    .addLoreLine("<red><bold>CLICK TO REMOVE")
                    .buildClickable(e -> {
                        if (cache.removeBlacklistedItem(mat)) {
                            cache.playSuccess(player);
                            open(player, plugin, cache, page);
                        }
                    });
            gui.setItem(slot++, cItem);
        }
    }

    private static void buildNavigationItems(AliienGUI gui, Player player, AliienItemBlacklist plugin, ItemsCache cache, int page, boolean hasNextPage, boolean hasPrevPage) {
        ClickableItem filler = getFiller();
        for (int i = 45; i < 54; i++) gui.setItem(i, filler);

        // Back to main menu
        gui.setItem(48, new ItemBuilder(Material.ARROW)
                .name("<red><bold>Back")
                .buildClickable(e -> {
                    cache.playClick(player);
                    MainMenuGUI.open(player, plugin, cache);
                }));

        // Previous page button
        if (hasPrevPage) {
            gui.setItem(45, new ItemBuilder(Material.PAPER)
                    .name("<yellow><bold>Previous Page")
                    .buildClickable(e -> {
                        cache.playClick(player);
                        open(player, plugin, cache, page - 1);
                    }));
        }

        ClickableItem addBtn = new ItemBuilder(Material.HOPPER)
                .name("<#55ff55><bold>Add Item")
                .addLoreLine("<gray>Hold the item you want to blacklist")
                .addLoreLine("<gray>in your hand, then click here!")
                .buildClickable(e -> handleAddAction(player, plugin, cache, page));
        gui.setItem(49, addBtn);

        // Next page button
        if (hasNextPage) {
            gui.setItem(53, new ItemBuilder(Material.PAPER)
                    .name("<yellow><bold>Next Page")
                    .buildClickable(e -> {
                        cache.playClick(player);
                        open(player, plugin, cache, page + 1);
                    }));
        }
    }

    private static void handleAddAction(Player player, AliienItemBlacklist plugin, ItemsCache cache, int page) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (heldItem.getType().isAir()) {
            cache.playError(player);
            MessageUtils.send(player, cache.getPrefix(), cache.getNotHoldingItemMsg());
            return;
        }

        if (cache.addBlacklistedItem(heldItem.getType())) {
            cache.playSuccess(player);
            open(player, plugin, cache, page); // Refresh page
            MessageUtils.send(player, cache.getPrefix(), cache.getAddedItemToBlacklistMsg(), "%item%", heldItem.getType().name());
        } else {
            cache.playError(player);
            MessageUtils.send(player, cache.getPrefix(), cache.getItemAlreadyBlacklistedMsg(), "%item%", heldItem.getType().name());
        }
    }
}