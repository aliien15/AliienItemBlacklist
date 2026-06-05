package com.aliiensmp.aliienItemBlacklist.services;

import com.aliiensmp.aliienItemBlacklist.config.Settings;
import com.aliiensmp.core.discord.DiscordWebhook;

public class DiscordLogger {

    public static void sendEmbed(String playerName, String materialName) {
        if (!Settings.DISCORD_WEBHOOK_ENABLED) return;

        DiscordWebhook webhook = new DiscordWebhook(Settings.DISCORD_WEBHOOK_LINK)
                .setTitle(Settings.DISCORD_WEBHOOK_TITLE)
                .setDescription(Settings.DISCORD_WEBHOOK_DESCRIPTION.replace("%player%", playerName).replace("%item%", materialName))
                .setColor(Settings.DISCORD_WEBHOOK_COLOR);

        webhook.sendAsync();
    }
}
