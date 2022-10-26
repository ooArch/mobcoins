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
                    double mobcoins = 0D;
                    switch (e.getEntityType()) {
                        case PIG:
                            mobcoins = config.getDouble("KillMob.PIG");
                            user.setMobcoins(user.getMobcoins() + mobcoins);
                            break;

                        case WOLF:
                            mobcoins = config.getDouble("KillMob.WOLF");
                            user.setMobcoins(user.getMobcoins() + mobcoins);
                            break;

                        case COW:
                            mobcoins = config.getDouble("KillMob.COW");
                            user.setMobcoins(user.getMobcoins() + mobcoins);
                            break;

                        case SHEEP:
                            mobcoins = config.getDouble("KillMob.SHEEP");
                            user.setMobcoins(user.getMobcoins() + mobcoins);
                            break;

                        case SPIDER:
                            mobcoins = config.getDouble("KillMob.SPIDER");
                            user.setMobcoins(user.getMobcoins() + mobcoins);
                            break;

                        case CAVE_SPIDER:
                            mobcoins = config.getDouble("KillMob.CAVE_SPIDER");
                            user.setMobcoins(user.getMobcoins() + mobcoins);
                            break;

                        case ZOMBIE:
                            mobcoins = config.getDouble("KillMob.ZOMBIE");
                            user.setMobcoins(user.getMobcoins() + mobcoins);
                            break;

                        case SKELETON:
                            mobcoins = config.getDouble("KillMob.SKELETON");
                            user.setMobcoins(user.getMobcoins() + mobcoins);
                            break;

                        case BLAZE:
                            mobcoins = config.getDouble("KillMob.BLAZE");
                            user.setMobcoins(user.getMobcoins() + mobcoins);
                            break;

                        case PIG_ZOMBIE:
                            mobcoins = config.getDouble("KillMob.PIG_ZOMBIE");
                            user.setMobcoins(user.getMobcoins() + mobcoins);
                            break;

                        case IRON_GOLEM:
                            mobcoins = config.getDouble("KillMob.IRON_GOLEM");
                            user.setMobcoins(user.getMobcoins() + mobcoins);
                            break;

                        case VILLAGER:
                            mobcoins = config.getDouble("KillMob.VILLAGER");
                            user.setMobcoins(user.getMobcoins() + mobcoins);
                            break;

                        case WITCH:
                            mobcoins = config.getDouble("KillMob.WITCH");
                            user.setMobcoins(user.getMobcoins() + mobcoins);
                            break;

                        case WITHER:
                            mobcoins = config.getDouble("KillMob.WITHER");
                            user.setMobcoins(user.getMobcoins() + mobcoins);
                            break;

                        default:
                            break;
                    }
                    Schedulers.async().runLater(() -> MobCoinsRepositories.MYSQL.insert(user), 5, TimeUnit.SECONDS);
                }
            }
        }
    }
}
