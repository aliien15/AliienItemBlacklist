package com.aliiensmp.aliienItemBlacklist.gui;

import static com.aliiensmp.aliienItemBlacklist.gui.MainMenuGUI.getFiller;

import com.aliiensmp.aliienItemBlacklist.AliienItemBlacklist;
import com.aliiensmp.aliienItemBlacklist.utils.ItemsCache;
import com.aliiensmp.core.items.ItemBuilder;
import com.aliiensmp.core.menu.AliienGUI;
import com.aliiensmp.core.menu.ClickableItem;
import com.aliiensmp.core.utils.DebugUtils;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class BlacklistGUI {

    private static final List<Integer> CONTENT_SLOTS = IntStream.range(0, 45).boxed().toList();

    public static void open(Player player, AliienItemBlacklist plugin, ItemsCache cache, int page) {
        AliienGUI gui =
                new AliienGUI("<#2b2d31><bold>ItemBlacklist <dark_gray>» <gray>Page %page%", 6);

        List<Material> items = new ArrayList<>(cache.getBlacklistedItems());
        int maxPerPage = CONTENT_SLOTS.size();

        boolean hasNextPage = items.size() > page * maxPerPage;
        boolean hasPrevPage = page > 1;

        gui.setItems(
                CONTENT_SLOTS,
                items,
                page,
                mat ->
                        new ItemBuilder(mat)
                                .addLoreLine("")
                                .addLoreLine("<red><bold>CLICK TO REMOVE")
                                .buildClickable(
                                        e -> {
                                            if (cache.removeBlacklistedItem(mat)) {
                                                DebugUtils.send(
                                                        "Player %player% removed %item% from the"
                                                                + " blacklist.",
                                                        "%player%",
                                                        player.getName(),
                                                        "%item%",
                                                        mat.name());
                                                cache.playSuccess(player);

                                                // Step back a page if the player just removed the
                                                // last item on the current page
                                                int newTotal = items.size() - 1;
                                                int targetPage =
                                                        (newTotal <= (page - 1) * maxPerPage
                                                                        && page > 1)
                                                                ? page - 1
                                                                : page;
                                                open(player, plugin, cache, targetPage);
                                            }
                                        }));

        buildNavigationItems(gui, player, plugin, cache, page, hasNextPage, hasPrevPage);
        gui.open(player, page);
    }

    private static void buildNavigationItems(
            AliienGUI gui,
            Player player,
            AliienItemBlacklist plugin,
            ItemsCache cache,
            int page,
            boolean hasNextPage,
            boolean hasPrevPage) {
        ClickableItem filler = getFiller();
        for (int i = 45; i < 54; i++) {
            gui.setItem(i, filler);
        }

        // Back to main menu
        gui.setItem(
                48,
                new ItemBuilder(Material.ARROW)
                        .name("<red><bold>Back")
                        .buildClickable(
                                e -> {
                                    cache.playClick(player);
                                    MainMenuGUI.open(player, plugin, cache);
                                }));

        // Previous page button
        if (hasPrevPage) {
            gui.setItem(
                    45,
                    new ItemBuilder(Material.PAPER)
                            .name("<yellow><bold>Previous Page")
                            .buildClickable(
                                    e -> {
                                        cache.playClick(player);
                                        open(player, plugin, cache, page - 1);
                                    }));
        }

        // Open inventory mirror submenu
        gui.setItem(
                49,
                new ItemBuilder(Material.HOPPER)
                        .name("<#55ff55><bold>Add Items")
                        .addLoreLine("<gray>Click to view and blacklist items")
                        .addLoreLine("<gray>currently in your inventory!")
                        .buildClickable(
                                e -> {
                                    cache.playClick(player);
                                    BlacklistAddGUI.open(player, plugin, cache, page);
                                }));

        // Next page button
        if (hasNextPage) {
            gui.setItem(
                    53,
                    new ItemBuilder(Material.PAPER)
                            .name("<yellow><bold>Next Page")
                            .buildClickable(
                                    e -> {
                                        cache.playClick(player);
                                        open(player, plugin, cache, page + 1);
                                    }));
        }
    }
}
