package com.github.merelysnow.mobcoins.listeners;

import com.github.merelysnow.mobcoins.MobCoinsPlugin;
import com.github.merelysnow.mobcoins.MobCoinsRepositories;
import com.github.merelysnow.mobcoins.database.MobCoinsDatabase;
import com.github.merelysnow.mobcoins.model.User;
import com.github.merelysnow.mobcoins.utils.ActionBar;
import lombok.val;
import me.lucko.helper.Schedulers;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PlayerListener implements Listener {

    @EventHandler
    void onJoin(PlayerJoinEvent e) {

        val p = e.getPlayer();
        User user = MobCoinsRepositories.CACHE_LOCAL.fetch(p.getName());

        if (user == null) {
            user = new User(p.getName(), 0.0);

            MobCoinsRepositories.CACHE_LOCAL.validate(user);
            MobCoinsRepositories.MYSQL.insert(user);
        }
    }

    @EventHandler
    void onKillEntity(EntityDeathEvent e) {

        val config = MobCoinsPlugin.plugin.getConfig();

        if (e.getEntity().getKiller() != null) {

            val p = e.getEntity().getKiller();
            User user = MobCoinsRepositories.CACHE_LOCAL.fetch(p.getName());

            if (user != null) {
                if (new Random().nextInt(100) >= 50) {
                    if(MobCoinsPlugin.plugin.getCache().containsKey(e.getEntityType())) {
                        user.setMobcoins(user.getMobcoins() + MobCoinsPlugin.plugin.getCache().getOrDefault(e.getEntityType(), 1.0));
                        Schedulers.async().runLater(() -> MobCoinsRepositories.MYSQL.insert(user), 5, TimeUnit.SECONDS);
                    }
                }
            }
        }
    }
}
