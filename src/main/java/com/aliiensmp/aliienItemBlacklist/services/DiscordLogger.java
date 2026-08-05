package com.aliiensmp.aliienItemBlacklist.services;

import com.aliiensmp.aliienItemBlacklist.config.Settings;
import com.aliiensmp.core.discord.DiscordWebhook;

public class DiscordLogger {

    /**
     * Send a message to a discord webhook whenever a player is caught with a blacklisted item
     *
     * @param playerName the player caught with a blacklisted item
     * @param materialName the blacklisted item that the player was caught with
     * @param playerAction the action/event that the players was doing/triggering
     * @requires {@code playerName != null && materialName != null}
     */
    public static void sendEmbed(String playerName, String materialName, String playerAction) {
        if (!Settings.DISCORD_WEBHOOK_ENABLED) return;

        DiscordWebhook webhook = new DiscordWebhook(Settings.DISCORD_WEBHOOK_LINK)
                .setTitle(Settings.DISCORD_WEBHOOK_TITLE)
                .setDescription(Settings.DISCORD_WEBHOOK_DESCRIPTION.replace("%player%", playerName).replace("%item%", materialName))
                .addField("Player Action:", playerAction, false)
                .setColor(Settings.DISCORD_WEBHOOK_COLOR)
                .setContent(Settings.DISCORD_WEBHOOK_ROLE, true);

        webhook.sendAsync();
    }
}
