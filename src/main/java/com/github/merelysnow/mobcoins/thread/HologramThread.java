package com.github.merelysnow.mobcoins.thread;

import com.github.merelysnow.mobcoins.MobCoinsPlugin;
import com.github.merelysnow.mobcoins.MobCoinsRepositories;
import com.github.merelysnow.mobcoins.model.User;
import com.github.merelysnow.mobcoins.utils.LuckPermsUtil;
import com.github.merelysnow.mobcoins.utils.StringUtils;
import me.filoghost.holographicdisplays.api.beta.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.beta.hologram.Hologram;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.NumberFormat;
import java.util.concurrent.atomic.AtomicInteger;

public class HologramThread extends BukkitRunnable {

    private final HolographicDisplaysAPI api;
    private final Hologram hologram;
    private final AtomicInteger position = new AtomicInteger(0);

    public HologramThread() {
        this.api = HolographicDisplaysAPI.get(MobCoinsPlugin.plugin);
        this.hologram = this.api.createHologram(StringUtils.deserializeLocation(MobCoinsPlugin.plugin.getConfig().getString("Locais.Holograma")).clone().add(0.0, 3.0, 0.0));
    }

    @Override
    public void run() {
        this.hologram.getLines().clear();
        this.position.set(0);

        hologram.getLines().appendText("§6§lRANKING DE MOBCOINS");
        hologram.getLines().appendText(" ");

        for(User all : MobCoinsRepositories.CACHE_LOCAL.getTop(5)) {
            position.incrementAndGet();
            hologram.getLines().appendText(String.format("§a%sº %s §7- §a%s Mobcoins", position.get(), LuckPermsUtil.getGroupPrefix(all.getName()) + all.getName(), NumberFormat.getInstance().format(all.getMobcoins())));
        }
    }
}
