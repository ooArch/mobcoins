package com.github.merelysnow.mobcoins.thread;

import com.github.merelysnow.mobcoins.MobCoinsPlugin;
import com.github.merelysnow.mobcoins.model.User;
import com.github.merelysnow.mobcoins.utils.LuckPermsUtil;
import com.github.merelysnow.mobcoins.utils.StringUtils;
import com.google.common.collect.Lists;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HologramThread extends BukkitRunnable {

    private final Hologram hologram;
    private final AtomicInteger position = new AtomicInteger(0);

    public HologramThread() {
        hologram = DHAPI.createHologram(
                "mobcoins-ranking",
                StringUtils.deserializeLocation(MobCoinsPlugin.getInstance().getConfig().getString("Locais.Holograma")).clone().add(0.0, 3.0, 0.0)
        );
    }

    @Override
    public void run() {
        this.position.set(0);

        List<String> lines = Lists.newArrayList();

        lines.add("§6§lRANKING DE MOBCOINS");
        lines.add("");

        for (User all : MobCoinsPlugin.getInstance().getUserController().getTop(5)) {
            position.incrementAndGet();
            lines.add(String.format(
                    "§a%sº %s §7- §a%s Mobcoins",
                    position.get(), LuckPermsUtil.getGroupPrefix(all.getName()) + all.getName(),
                    NumberFormat.getInstance().format(all.getMobcoins()))
            );
        }

        DHAPI.setHologramLines(hologram, lines);
    }
}
