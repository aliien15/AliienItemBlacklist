package com.aliiensmp.aliienItemBlacklist.config;

import com.aliiensmp.core.config.Key;
import org.jetbrains.annotations.NotNull;

public class Messages {

    @Key("prefix")
    public static @NotNull String PREFIX = "<dark_red><bold>ITEMBLACKLIST</bold></dark_red> <dark_gray>»</dark_gray> ";

    @Key("alert")
    public static @NotNull String ALERT = "<red><bold>THE PLAYER %player% HAD A BLACKLISTED ITEM! (%item%)";

    @Key("no-permission")
    public static @NotNull String NO_PERMISSION = "<red>You do not have permission to perform this command!";

    @Key("reload")
    public static @NotNull String RELOAD = "<green>AliienItemBlacklist has been reloaded successfully!";

    @Key("fail-reload")
    public static @NotNull String FAIL_RELOAD = "<red>There was an error while reloading AliienItemBlacklist!";

    @Key("not-holding-item")
    public static @NotNull String NOT_HOLDING_ITEM = "<red>You must be holding an item to do this!";

    @Key("added-item-to-blacklist")
    public static @NotNull String ADDED_ITEM_TO_BLACKLIST = "<green>The item %item% has successfully been added to the blacklist!";

    @Key("item-already-blacklisted")
    public static @NotNull String ITEM_ALREADY_BLACKLISTED = "<red>The item %item% is already blacklisted!";

    @Key("new-version")
    public static @NotNull String NEW_VERSION = "<green>A new AliienItemBlacklist version is now available!";
}