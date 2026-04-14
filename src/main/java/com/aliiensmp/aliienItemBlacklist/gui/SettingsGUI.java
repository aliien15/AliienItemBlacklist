package com.aliiensmp.aliienItemBlacklist.gui;

import com.aliiensmp.aliienItemBlacklist.AliienItemBlacklist;
import com.aliiensmp.aliienItemBlacklist.utils.ItemsCache;
import com.aliiensmp.core.items.ItemBuilder;
import com.aliiensmp.core.menu.AliienGUI;
import com.aliiensmp.core.menu.ClickableItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static com.aliiensmp.aliienItemBlacklist.gui.MainMenuGUI.getFiller;

public class SettingsGUI {

    public static void open(Player player, AliienItemBlacklist plugin, ItemsCache cache) {
        AliienGUI gui = new AliienGUI("<#2b2d31><bold>ItemBlacklist <dark_gray>» <gray>Settings", 3);

        populateItems(gui, player, plugin, cache);

        gui.open(player, 1);
    }

    private static void populateItems(AliienGUI gui, Player player, AliienItemBlacklist plugin, ItemsCache cache) {
        ClickableItem filler = getFiller();
        for (int i = 0; i < 27; i++) gui.setItem(i, filler);

        String on = "<green>Enabled";
        String off = "<red>Disabled";

        // Staff alerts toggle
        ClickableItem alertsBtn = new ItemBuilder(Material.BELL)
                .name("<#55ffff><bold>Show Alerts")
                .addLoreLine("<gray>Sends an alert to online staff when a")
                .addLoreLine("<gray>player handles a blacklisted item.")
                .addLoreLine("")
                .addLoreLine("<gray>Current Status: " + (cache.isShowAlerts() ? on : off))
                .addLoreLine("")
                .addLoreLine("<yellow>Click to toggle!")
                .buildClickable(e -> toggleSetting(player, cache::toggleAlerts, plugin, cache));

        // Strict mode toggle
        ClickableItem strictBtn = new ItemBuilder(Material.IRON_BARS)
                .name("<#ff5555><bold>Strict Mode")
                .addLoreLine("<gray>If enabled, NO ONE can use blacklisted")
                .addLoreLine("<gray>items, completely ignoring bypass permissions.")
                .addLoreLine("")
                .addLoreLine("<gray>Current Status: " + (cache.isStrictMode() ? on : off))
                .addLoreLine("")
                .addLoreLine("<yellow>Click to toggle!")
                .buildClickable(e -> toggleSetting(player, cache::toggleStrictMode, plugin, cache));

        // Log toggle
        ClickableItem loggingBtn = new ItemBuilder(Material.PAPER)
                .name("<#ffff55><bold>Enable Logging")
                .addLoreLine("<gray>Records all interactions with blacklisted")
                .addLoreLine("<gray>items into a local logs.txt file.")
                .addLoreLine("<dark_gray><i>*(Requires a server reboot to apply)*</i>")
                .addLoreLine("")
                .addLoreLine("<gray>Current Status: " + (cache.isEnableLogging() ? on : off))
                .addLoreLine("")
                .addLoreLine("<yellow>Click to toggle!")
                .buildClickable(e -> toggleSetting(player, cache::toggleEnableLogging, plugin, cache));

        // New updates toggle
        ClickableItem updatesBtn = new ItemBuilder(Material.EMERALD)
                .name("<#55ff55><bold>Check For Updates")
                .addLoreLine("<gray>Notifies staff when a new plugin")
                .addLoreLine("<gray>update is available on startup.")
                .addLoreLine("<dark_gray><i>*(Requires a server reboot to apply)*</i>")
                .addLoreLine("")
                .addLoreLine("<gray>Current Status: " + (cache.isCheckForUpdates() ? on : off))
                .addLoreLine("")
                .addLoreLine("<yellow>Click to toggle!")
                .buildClickable(e -> toggleSetting(player, cache::toggleCheckForUpdates, plugin, cache));

        // Sounds toggle
        ClickableItem soundsBtn = new ItemBuilder(Material.JUKEBOX)
                .name("<#ff55ff><bold>GUI Sounds")
                .addLoreLine("<gray>Toggles all audio feedback")
                .addLoreLine("<gray>for the plugin menus.")
                .addLoreLine("")
                .addLoreLine("<gray>Current Status: " + (cache.isSoundsEnabled() ? on : off))
                .addLoreLine("")
                .addLoreLine("<yellow>Click to toggle!")
                .buildClickable(e -> toggleSetting(player, cache::toggleSoundsEnabled, plugin, cache));

        ClickableItem backBtn = new ItemBuilder(Material.ARROW)
                .name("<red><bold>Back")
                .buildClickable(e -> {
                    cache.playClick(player);
                    MainMenuGUI.open(player, plugin, cache);
                });

        gui.setItem(11, alertsBtn);
        gui.setItem(12, strictBtn);
        gui.setItem(13, loggingBtn);
        gui.setItem(14, updatesBtn);
        gui.setItem(15, soundsBtn);
        gui.setItem(22, backBtn);
    }

    private static void toggleSetting(Player player, Runnable task, AliienItemBlacklist plugin, ItemsCache cache) {
        task.run();
        cache.playSuccess(player);
        open(player, plugin, cache);
    }
}