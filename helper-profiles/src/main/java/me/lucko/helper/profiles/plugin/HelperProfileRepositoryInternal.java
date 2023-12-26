package me.lucko.helper.profiles.plugin;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HelperProfileRepositoryInternal {
    final Cache<UUID, ImmutableProfile> profileMap = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterAccess(6, TimeUnit.HOURS)
            .build();

    HelperProfileRepositoryInternal() { /* package private */ }

    // 连接池
    // 缓存
}
