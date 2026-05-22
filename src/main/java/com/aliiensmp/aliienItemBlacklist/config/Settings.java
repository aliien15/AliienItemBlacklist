package com.aliiensmp.aliienItemBlacklist.config;

import com.aliiensmp.core.config.Key;
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
    public static String SOUND_CLICK = "none";

    @Key("sounds.success")
    public static String SOUND_SUCCESS = "none";

    @Key("sounds.error")
    public static String SOUND_ERROR = "none";

    @Key("sounds.alert")
    public static String SOUND_ALERT = "none";

    @Key("disabled-worlds")
    public static List<String> DISABLED_WORLDS = List.of();
}