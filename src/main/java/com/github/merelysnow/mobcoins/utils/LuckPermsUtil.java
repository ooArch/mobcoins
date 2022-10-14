package com.github.merelysnow.mobcoins.utils;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;

import java.util.Objects;

public class LuckPermsUtil {

    public static String getGroupPrefix(Player player) {
        if (player == null) {
            return "§7";
        } else {
            User user = LuckPermsProvider.get().getPlayerAdapter(Player.class).getUser(player);
            if (user == null) {
                return "&7";
            } else {
                Group group = LuckPermsProvider.get().getGroupManager().getGroup(user.getPrimaryGroup());
                if (group == null) {
                    return "&7";
                } else {
                    return Objects.requireNonNull(group.getCachedData().getMetaData().getPrefix()).replace("&","§");
                }
            }
        }
    }

    public static String getGroupColor(Player player) {
        return getGroupPrefix(player).substring(0, 2);
    }
}
