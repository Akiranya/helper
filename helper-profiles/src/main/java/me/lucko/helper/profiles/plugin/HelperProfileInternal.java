package me.lucko.helper.profiles.plugin;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.lucko.helper.sql.Sql;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class HelperProfileInternal {
    static final String CREATE =
            "CREATE TABLE IF NOT EXISTS {table} (" +
            "`uniqueid` BINARY(16) NOT NULL PRIMARY KEY, " +
            "`name` VARCHAR(16) NOT NULL, " +
            "`lastupdate` TIMESTAMP NOT NULL)";

    static final String INSERT = "INSERT INTO {table} VALUES(UNHEX(?), ?, ?) ON DUPLICATE KEY UPDATE `name` = ?, `lastupdate` = ?";
    static final String SELECT_UID = "SELECT `name`, `lastupdate` FROM {table} WHERE `uniqueid` = UNHEX(?)";
    static final String SELECT_NAME = "SELECT HEX(`uniqueid`) AS `canonicalid`, `name`, `lastupdate` FROM {table} WHERE `name` = ? ORDER BY `lastupdate` DESC LIMIT 1";
    static final String SELECT_ALL = "SELECT HEX(`uniqueid`) AS `canonicalid`, `name`, `lastupdate` FROM {table}";
    static final String SELECT_ALL_RECENT = "SELECT HEX(`uniqueid`) AS `canonicalid`, `name`, `lastupdate` FROM {table} ORDER BY `lastupdate` DESC LIMIT ?";
    static final String SELECT_ALL_UIDS = "SELECT HEX(`uniqueid`) AS `canonicalid`, `name`, `lastupdate` FROM {table} WHERE `uniqueid` IN %s";
    static final String SELECT_ALL_NAMES = "SELECT HEX(`uniqueid`) AS `canonicalid`, `name`, `lastupdate` FROM {table} WHERE `name` IN %s GROUP BY `name` ORDER BY `lastupdate` DESC";

    static final Pattern MINECRAFT_USERNAME_PATTERN = Pattern.compile("^\\w{3,16}$");

    final Cache<UUID, ImmutableProfile> profileMap = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterAccess(6, TimeUnit.HOURS)
            .build();
    final Sql sql;
    final String tableName;
    final int preloadAmount;

    /* package private */ HelperProfileInternal(
            Sql sql,
            String tableName,
            int preloadAmount
    ) {
        this.sql = sql;
        this.tableName = tableName;
        this.preloadAmount = preloadAmount;
    }
}
