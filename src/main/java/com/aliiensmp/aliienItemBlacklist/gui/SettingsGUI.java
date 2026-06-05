package com.aliiensmp.aliienItemBlacklist.gui;

import com.aliiensmp.aliienItemBlacklist.AliienItemBlacklist;
import com.aliiensmp.aliienItemBlacklist.config.Settings;
import com.aliiensmp.aliienItemBlacklist.utils.ItemsCache;
import com.aliiensmp.core.items.ItemBuilder;
import com.aliiensmp.core.menu.AliienGUI;
import com.aliiensmp.core.menu.ClickableItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static com.aliiensmp.aliienItemBlacklist.gui.MainMenuGUI.getFiller;

public class SettingsGUI {

    public static void open(Player player, AliienItemBlacklist plugin, ItemsCache cache) {
        AliienGUI gui = new AliienGUI("<#2b2d31><bold>ItemBlacklist <dark_gray>» <gray>Settings", 5);

        populateItems(gui, player, plugin, cache);

        gui.open(player, 1);
    }

    private static void populateItems(AliienGUI gui, Player player, AliienItemBlacklist plugin, ItemsCache cache) {
        ClickableItem filler = getFiller();
        // 5 rows * 9 slots = 45 total slots (0-44)
        for (int i = 0; i < 45; i++) gui.setItem(i, filler);

        String on = "<green>Enabled";
        String off = "<red>Disabled";

        // Staff alerts toggle
        ClickableItem alertsBtn = new ItemBuilder(Material.BELL)
                .name("<#55ffff><bold>Show Alerts")
                .addLoreLine("<gray>Sends an alert to online staff when a")
                .addLoreLine("<gray>player handles a blacklisted item.")
                .addLoreLine("")
                .addLoreLine("<gray>Current Status: " + (Settings.SHOW_ALERTS ? on : off))
                .addLoreLine("")
                .addLoreLine("<yellow>Click to toggle!")
                .buildClickable(e -> toggleSetting(player, cache::toggleAlerts, plugin, cache));

        // Strict mode toggle
        ClickableItem strictBtn = new ItemBuilder(Material.IRON_BARS)
                .name("<#ff5555><bold>Strict Mode")
                .addLoreLine("<gray>If enabled, NO ONE can use blacklisted")
                .addLoreLine("<gray>items, completely ignoring bypass permissions.")
                .addLoreLine("")
                .addLoreLine("<gray>Current Status: " + (Settings.STRICT_MODE ? on : off))
                .addLoreLine("")
                .addLoreLine("<yellow>Click to toggle!")
                .buildClickable(e -> toggleSetting(player, cache::toggleStrictMode, plugin, cache));

        // Log toggle
        ClickableItem loggingBtn = new ItemBuilder(Material.PAPER)
                .name("<#ffff55><bold>Enable Logging")
                .addLoreLine("<gray>Records all interactions with blacklisted")
                .addLoreLine("<gray>items into a local logs.txt file.")
                .addLoreLine("")
                .addLoreLine("<gray>Current Status: " + (Settings.ENABLE_LOGGING ? on : off))
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
                .addLoreLine("<gray>Current Status: " + (Settings.CHECK_FOR_UPDATES ? on : off))
                .addLoreLine("")
                .addLoreLine("<yellow>Click to toggle!")
                .buildClickable(e -> toggleSetting(player, cache::toggleCheckForUpdates, plugin, cache));

        // Sounds toggle
        ClickableItem soundsBtn = new ItemBuilder(Material.JUKEBOX)
                .name("<#ff55ff><bold>GUI Sounds")
                .addLoreLine("<gray>Toggles all audio feedback")
                .addLoreLine("<gray>for the plugin menus.")
                .addLoreLine("")
                .addLoreLine("<gray>Current Status: " + (Settings.SOUNDS_ENABLED ? on : off))
                .addLoreLine("")
                .addLoreLine("<yellow>Click to toggle!")
                .buildClickable(e -> toggleSetting(player, cache::toggleSoundsEnabled, plugin, cache));

        // Discord webhooks toggle
        ClickableItem webhooksBtn = new ItemBuilder(Material.ENDER_PEARL)
                .name("<#7289da><bold>Discord Alerts")
                .addLoreLine("<gray>Toggles routing blacklisted item")
                .addLoreLine("<gray>alerts directly to Discord.")
                .addLoreLine("")
                .addLoreLine("<gray>Current Status: " + (Settings.DISCORD_WEBHOOK_ENABLED ? on : off))
                .addLoreLine("")
                .addLoreLine("<yellow>Click to toggle!")
                .buildClickable(e -> toggleSetting(player, cache::toggleWebhooksEnabled, plugin, cache));

        // Back button
        ClickableItem backBtn = new ItemBuilder(Material.ARROW)
                .name("<red><bold>Back")
                .buildClickable(e -> {
                    cache.playClick(player);
                    MainMenuGUI.open(player, plugin, cache);
                });

        // Second row
        gui.setItem(11, alertsBtn);
        gui.setItem(13, strictBtn);
        gui.setItem(15, loggingBtn);

        // Third row
        gui.setItem(20, updatesBtn);
        gui.setItem(22, soundsBtn);
        gui.setItem(24, webhooksBtn);

        // Fifth row
        gui.setItem(40, backBtn);
    }

    private static void toggleSetting(Player player, Runnable task, AliienItemBlacklist plugin, ItemsCache cache) {
        task.run();
        cache.playSuccess(player);
        open(player, plugin, cache);
    }
}