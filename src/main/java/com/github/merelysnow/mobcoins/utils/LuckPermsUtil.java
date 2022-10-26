package com.github.merelysnow.mobcoins.utils;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;

import java.util.Objects;

public class LuckPermsUtil {

    public static String getGroupPrefix(String name) {
        User user = LuckPermsProvider.get().getUserManager().getUser(name);
        if (user == null) {
            return "&7";
        } else {
            Group group = LuckPermsProvider.get().getGroupManager().getGroup(user.getPrimaryGroup());
            if (group == null) {
                return "&7";
            } else {
                return Objects.requireNonNull(group.getCachedData().getMetaData().getPrefix()).replace("&", "§");
            }
        }
    }
}
