package com.github.merelysnow.mobcoins.cache.local;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.merelysnow.mobcoins.model.User;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MobCoinsLocalCache {

    private final @NonNull Cache<String, User> cache = Caffeine.newBuilder().build();
    private static final Comparator<User> USER_COMPARATOR = Comparator.comparingDouble(User::getMobcoins);

    public void validate(User user) {
        cache.put(user.getName(), user);
    }

    public User fetch(String name) {
        return cache.getIfPresent(name);
    }

    public void invalidate(User user) {
        cache.invalidate(user);
    }

    public List<User> getTop(int limit) {
        return cache.asMap()
                .values()
                .stream()
                .sorted(USER_COMPARATOR.reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
