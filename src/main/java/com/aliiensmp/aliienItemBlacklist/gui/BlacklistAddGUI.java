package com.aliiensmp.aliienItemBlacklist.gui;

import static com.aliiensmp.aliienItemBlacklist.gui.MainMenuGUI.getFiller;

import com.aliiensmp.aliienItemBlacklist.AliienItemBlacklist;
import com.aliiensmp.aliienItemBlacklist.config.Messages;
import com.aliiensmp.aliienItemBlacklist.utils.ItemsCache;
import com.aliiensmp.core.items.ItemBuilder;
import com.aliiensmp.core.menu.AliienGUI;
import com.aliiensmp.core.menu.ClickableItem;
import com.aliiensmp.core.utils.DebugUtils;
import com.aliiensmp.core.utils.MessageUtils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

public class BlacklistAddGUI {

    private static final List<Integer> CONTENT_SLOTS = IntStream.range(0, 45).boxed().toList();

    public static void open(
            Player player, AliienItemBlacklist plugin, ItemsCache cache, int returnPage) {
        AliienGUI gui =
                new AliienGUI(
                        "<#2b2d31><bold>Blacklist <dark_gray>» <gray>Select from Inventory", 6);

        Set<Material> currentlyBlacklisted = cache.getBlacklistedItems();

        // Collect all unique, valid materials from the player's inventory that aren't blacklisted
        // yet
        List<Material> availableMaterials =
                Arrays.stream(player.getInventory().getContents())
                        .filter(Objects::nonNull)
                        .map(ItemStack::getType)
                        .filter(mat -> !mat.isAir() && !currentlyBlacklisted.contains(mat))
                        .distinct()
                        .toList();

        if (availableMaterials.isEmpty()) {
            gui.setItem(
                    22,
                    new ItemBuilder(Material.BARRIER)
                            .name("<red><bold>No Eligible Items Found")
                            .addLoreLine("<gray>Your inventory does not contain any")
                            .addLoreLine("<gray>un-blacklisted items to add.")
                            .buildClickable(e -> cache.playError(player)));
        } else {
            gui.setItems(
                    CONTENT_SLOTS,
                    availableMaterials,
                    1,
                    mat ->
                            new ItemBuilder(mat)
                                    .addLoreLine("")
                                    .addLoreLine("<#55ff55><bold>CLICK TO BLACKLIST")
                                    .buildClickable(
                                            e -> {
                                                if (cache.addBlacklistedItem(mat)) {
                                                    DebugUtils.send(
                                                            "Player %player% added %item% to the"
                                                                    + " blacklist via GUI.",
                                                            "%player%",
                                                            player.getName(),
                                                            "%item%",
                                                            mat.name());
                                                    cache.playSuccess(player);
                                                    MessageUtils.send(
                                                            player,
                                                            Messages.PREFIX,
                                                            Messages.ADDED_ITEM_TO_BLACKLIST,
                                                            "%item%",
                                                            mat.name());
                                                    open(player, plugin, cache, returnPage);
                                                } else {
                                                    cache.playError(player);
                                                    MessageUtils.send(
                                                            player,
                                                            Messages.PREFIX,
                                                            Messages.ITEM_ALREADY_BLACKLISTED,
                                                            "%item%",
                                                            mat.name());
                                                }
                                            }));
        }

        // Bottom navigation bar
        ClickableItem filler = getFiller();
        for (int i = 45; i < 54; i++) {
            gui.setItem(i, filler);
        }

        gui.setItem(
                49,
                new ItemBuilder(Material.ARROW)
                        .name("<red><bold>Back to Blacklist")
                        .buildClickable(
                                e -> {
                                    cache.playClick(player);
                                    BlacklistGUI.open(player, plugin, cache, returnPage);
                                }));

        gui.open(player, 1);
    }
}
