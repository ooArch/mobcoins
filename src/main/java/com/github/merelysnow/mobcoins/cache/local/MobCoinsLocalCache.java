package com.github.merelysnow.mobcoins.cache.local;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.merelysnow.mobcoins.model.User;
import org.checkerframework.checker.nullness.qual.NonNull;

public class MobCoinsLocalCache {

    private final @NonNull Cache<String, User> cache = Caffeine.newBuilder().build();

    public void validate(User user) {
        cache.put(user.getName(), user);
    }

    public User fetch(String name) {
        return cache.getIfPresent(name);
    }

    public void invalidate(User user) {
        cache.invalidate(user);
    }
}
