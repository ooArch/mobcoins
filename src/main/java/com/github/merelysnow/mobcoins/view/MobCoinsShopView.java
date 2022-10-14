package com.github.merelysnow.mobcoins.view;

import com.github.merelysnow.mobcoins.MobCoinsPlugin;
import com.github.merelysnow.mobcoins.MobCoinsRepositories;
import com.github.merelysnow.mobcoins.model.User;
import com.github.merelysnow.mobcoins.utils.ActionBar;
import com.github.merelysnow.mobcoins.utils.ItemBuilder;
import com.github.merelysnow.mobcoins.utils.LuckPermsUtil;
import me.lucko.helper.Schedulers;
import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.ViewContext;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.text.NumberFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MobCoinsShopView extends View {

    public MobCoinsShopView() {
        super(5, "Loja de MobCoins");
        setCancelOnClick(true);
    }

    @Override
    protected void onRender(@org.jetbrains.annotations.NotNull ViewContext context) {

        User user = context.get("user");

        context.slot(4, new ItemBuilder(Material.SKULL_ITEM, 1, (short)3)
                .setName(LuckPermsUtil.getGroupPrefix(context.getPlayer()) + context.getPlayer().getName())
                .setLore("",
                        " §fMobcoins: §e" + NumberFormat.getInstance().format(user.getMobcoins()),
                        "")
                .setSkullOwner(context.getPlayer().getName())
                .build());

        MobCoinsPlugin.plugin.getStoreDAO().getItem().entrySet().stream().map(Map.Entry::getValue).forEach(store -> {
            context.slot(store.getSlot(), store.getIcon())
                    .onClick(ctx -> {

                        if(user.getMobcoins() < store.getPrice()) {
                            context.getPlayer().sendMessage("§cVocê não possui mobcoins o suficiente.");
                            context.getPlayer().playSound(context.getPlayer().getLocation(), Sound.VILLAGER_NO, 1.5f, 1.5f);
                            return;
                        }

                        user.setMobcoins(user.getMobcoins() - store.getPrice());
                        context.getPlayer().playSound(context.getPlayer().getLocation(), Sound.LEVEL_UP, 1.5f, 1.5f);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), store.getCommand().replace("{player}", context.getPlayer().getName()));
                        Schedulers.async().runLater(() -> MobCoinsRepositories.MYSQL.insert(user),  5, TimeUnit.SECONDS);
                        ActionBar.sendToPlayer(context.getPlayer(), "§eVocê adquiriu o item §f" + store.getName() + " §epor §f" + NumberFormat.getInstance().format(store.getPrice()) + " §eMobCoins");
                    });
        });
    }
}
