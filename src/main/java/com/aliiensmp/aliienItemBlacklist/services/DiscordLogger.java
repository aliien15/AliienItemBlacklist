package com.aliiensmp.aliienItemBlacklist.services;

import com.aliiensmp.aliienItemBlacklist.config.Settings;
import com.aliiensmp.core.discord.DiscordWebhook;
import com.aliiensmp.core.utils.DebugUtils;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;
import java.util.stream.Collectors;

public class DiscordLogger {

    /**
     * Send a message to a discord webhook whenever a player is caught with a blacklisted item
     *
     * @param player the player caught with a blacklisted item
     * @param item the blacklisted item that the player was caught with
     * @param playerAction the action/event that the players was doing/triggering
     * @requires {@code player != null && item != null}
     */
    public static void sendEmbed(Player player, ItemStack item, String playerAction) {
        if (!Settings.DISCORD_WEBHOOK_ENABLED) {
            DebugUtils.send(
                    "Not sending a discord webhook messsage for blacklisted item, as this feature"
                            + " is disabled in settings.yml");
            return;
        }

        DebugUtils.send("Sending discord webhook notification for blacklisted item async...");

        Location playerLocation = player.getLocation();
        ItemMeta itemMeta = item.getItemMeta();

        String lore =
                Optional.ofNullable(itemMeta)
                        .map(ItemMeta::lore)
                        .filter(list -> !list.isEmpty())
                        .map(
                                list ->
                                        list.stream()
                                                .map(
                                                        component ->
                                                                PlainTextComponentSerializer
                                                                        .plainText()
                                                                        .serialize(component))
                                                .collect(Collectors.joining("\n")))
                        .orElse("None");

        String enchants =
                Optional.ofNullable(itemMeta)
                        .map(ItemMeta::getEnchants)
                        .filter(map -> !map.isEmpty())
                        .map(
                                map ->
                                        map.entrySet().stream()
                                                .map(
                                                        entry ->
                                                                entry.getKey().getKey().getKey()
                                                                        + " (Lvl "
                                                                        + entry.getValue()
                                                                        + ")")
                                                .collect(Collectors.joining("\n")))
                        .orElse("None");

        String name =
                Optional.ofNullable(itemMeta)
                        .filter(ItemMeta::hasDisplayName)
                        .map(ItemMeta::displayName)
                        .or(
                                () ->
                                        Optional.ofNullable(itemMeta)
                                                .filter(ItemMeta::hasItemName)
                                                .map(ItemMeta::itemName))
                        .map(
                                component ->
                                        PlainTextComponentSerializer.plainText()
                                                .serialize(component))
                        .orElse("No custom item name");

        String customModelData =
                (itemMeta != null && itemMeta.hasCustomModelData())
                        ? String.valueOf(itemMeta.getCustomModelData())
                        : "None";

        DiscordWebhook webhook =
                new DiscordWebhook(Settings.DISCORD_WEBHOOK_LINK)
                        .setTitle(Settings.DISCORD_WEBHOOK_TITLE)
                        .setDescription(
                                Settings.DISCORD_WEBHOOK_DESCRIPTION
                                        .replace("%player%", player.getName())
                                        .replace("%item%", item.getType().name()))
                        .addField("Player Action:", playerAction, false)
                        .addField(
                                "Player Location:",
                                "X: "
                                        + playerLocation.getBlockX()
                                        + ", Y: "
                                        + playerLocation.getBlockY()
                                        + ", Z: "
                                        + playerLocation.getBlockZ()
                                        + ", World: "
                                        + playerLocation.getWorld().getName(),
                                false)
                        .addField("Item Name: ", name, false)
                        .addField("Item Lore: ", lore, false)
                        .addField("Item Enchants: ", enchants, false)
                        .addField("Item Custom Model Data: ", customModelData, false)
                        .setColor(Settings.DISCORD_WEBHOOK_COLOR)
                        .setContent(Settings.DISCORD_WEBHOOK_ROLE, true);

        webhook.sendAsync();
    }
}
