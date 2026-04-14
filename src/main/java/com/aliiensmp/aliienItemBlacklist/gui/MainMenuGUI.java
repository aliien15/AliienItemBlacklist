package com.aliiensmp.aliienItemBlacklist.gui;

import com.aliiensmp.aliienItemBlacklist.AliienItemBlacklist;
import com.aliiensmp.aliienItemBlacklist.utils.ItemsCache;
import com.aliiensmp.core.items.ItemBuilder;
import com.aliiensmp.core.menu.AliienGUI;
import com.aliiensmp.core.menu.ClickableItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MainMenuGUI {

    public static void open(Player player, AliienItemBlacklist plugin, ItemsCache cache) {
        AliienGUI gui = new AliienGUI("<#2b2d31><bold>ItemBlacklist <dark_gray>» <gray>Dashboard", 3);

        populateItems(gui, player, plugin, cache);

        gui.open(player, 1);
    }

    private static void populateItems(AliienGUI gui, Player player, AliienItemBlacklist plugin, ItemsCache cache) {
        ClickableItem filler = getFiller();
        for (int i = 0; i < 27; i++) gui.setItem(i, filler);

        ClickableItem blacklistBtn = new ItemBuilder(Material.BEDROCK)
                .name("<#ff5555><bold>Manage Blacklist")
                .addLoreLine("<gray>View, add, or remove items")
                .addLoreLine("<gray>from the server blacklist.")
                .addLoreLine("")
                .addLoreLine("<yellow>Click to open!")
                .buildClickable(e -> {
                    cache.playClick(player);
                    BlacklistGUI.open(player, plugin, cache, 1);
                });

        ClickableItem settingsBtn = new ItemBuilder(Material.COMPARATOR)
                .name("<#55ff55><bold>Plugin Settings")
                .addLoreLine("<gray>Configure alerts, strict mode,")
                .addLoreLine("<gray>logging, and updates.")
                .addLoreLine("")
                .addLoreLine("<yellow>Click to open!")
                .buildClickable(e -> {
                    cache.playClick(player);
                    SettingsGUI.open(player, plugin, cache);
                });

        gui.setItem(11, blacklistBtn);
        gui.setItem(15, settingsBtn);
    }

    protected static ClickableItem getFiller() {
        return ClickableItem.empty(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").build());
    }
}