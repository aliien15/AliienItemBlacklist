package com.aliiensmp.aliienItemBlacklist.config;

import com.aliiensmp.core.config.Key;

public class Messages {
    @Key("prefix")
    public static String PREFIX = "<dark_red><bold>PUNISH</bold></dark_red> <dark_gray>»</dark_gray> ";

    @Key("alert")
    public static String ALERT = "<red><bold>THE PLAYER %player% HAD A BLACKLISTED ITEM! (%item%)";

    @Key("no-permission")
    public static String NO_PERMISSION = "<red>You do not have permission to perform this command!";

    @Key("reload")
    public static String RELOAD = "<green>AliienItemBlacklist has been reloaded successfully!";

    @Key("fail-reload")
    public static String FAIL_RELOAD = "<red>There was an error while reloading AliienItemBlacklist!";

    @Key("not-holding-item")
    public static String NOT_HOLDING_ITEM = "<red>You must be holding an item to do this!";

    @Key("added-item-to-blacklist")
    public static String ADDED_ITEM_TO_BLACKLIST = "<green>The item %item% has successfully been added to the blacklist!";

    @Key("item-already-blacklisted")
    public static String ITEM_ALREADY_BLACKLISTED = "<red>The item %item% is already blacklisted!";

    @Key("new-version")
    public static String NEW_VERSION = "<green>A new AliienItemBlacklist version is now available!";
}