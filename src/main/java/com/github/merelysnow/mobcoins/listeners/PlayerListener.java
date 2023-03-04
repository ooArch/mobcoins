package com.github.merelysnow.mobcoins.listeners;

import com.github.merelysnow.mobcoins.MobCoinsPlugin;
import com.github.merelysnow.mobcoins.model.User;
import lombok.val;
import me.lucko.helper.Schedulers;
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

        Player p = e.getPlayer();
        User user = MobCoinsPlugin.getInstance().getUserController().get(p.getName());

        if (user == null) {
            user = new User(p.getName(), 0.0);

            MobCoinsPlugin.getInstance().getUserController().registerUser(user);
        }
    }

    @EventHandler
    void onKillEntity(EntityDeathEvent e) {

        if (e.getEntity().getKiller() != null) {

            val p = e.getEntity().getKiller();
            User user = MobCoinsPlugin.getInstance().getUserController().get(p.getName());

            if (user != null) {
                if (new Random().nextInt(100) >= 50) {
                    user.setMobcoins(user.getMobcoins() + MobCoinsPlugin.getInstance().getEntityController().getCache().getOrDefault(e.getEntityType(), 1.0));
                    MobCoinsPlugin.getInstance().getMobCoinsDatabase().insert(user);
                }
            }
        }
    }
}
