package com.aliiensmp.aliienItemBlacklist.config;

import com.aliiensmp.core.config.Key;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Settings {

    @Key("show-alerts")
    public static boolean SHOW_ALERTS = true;

    @Key("strict-mode")
    public static boolean STRICT_MODE = false;

    @Key("enable-logging")
    public static boolean ENABLE_LOGGING = true;

    @Key("check-for-updates")
    public static boolean CHECK_FOR_UPDATES = true;

    @Key("sounds.enabled")
    public static boolean SOUNDS_ENABLED = true;

    @Key("sounds.click")
    public static @NotNull String SOUND_CLICK = "none";

    @Key("sounds.success")
    public static @NotNull String SOUND_SUCCESS = "none";

    @Key("sounds.error")
    public static @NotNull String SOUND_ERROR = "none";

    @Key("sounds.alert")
    public static @NotNull String SOUND_ALERT = "none";

    @Key("disabled-worlds")
    public static @NotNull List<String> DISABLED_WORLDS = List.of();

    @Key("discord-webhook.enabled")
    public static boolean DISCORD_WEBHOOK_ENABLED = false;

    @Key("discord-webhook.url")
    public static @NotNull String DISCORD_WEBHOOK_LINK = "";

    @Key("discord-webhook.title")
    public static @NotNull String DISCORD_WEBHOOK_TITLE = "⚠️ Blacklisted Item Deleted!";

    @Key("discord-webhook.description")
    public static @NotNull String DISCORD_WEBHOOK_DESCRIPTION = "**%player%** has just been caught with a **blacklisted item** (%item%)";

    @Key("discord-webhook.color")
    public static @NotNull String DISCORD_WEBHOOK_COLOR = "#FF0F0F";
}