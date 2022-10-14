package com.github.merelysnow.mobcoins.commands;

import com.github.merelysnow.mobcoins.MobCoinsPlugin;
import com.github.merelysnow.mobcoins.MobCoinsRepositories;
import com.github.merelysnow.mobcoins.model.User;
import com.github.merelysnow.mobcoins.view.MobCoinsShopView;
import com.google.common.collect.ImmutableMap;
import lombok.val;
import me.lucko.helper.Schedulers;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

public class MobCoinsCommand {

    @Command(
            name = "mobcoins",
            aliases = "mb",
            target = CommandTarget.PLAYER
    )
    public void MobCoinsCommand(Context<Player> context) {

        val p = context.getSender();
        User user = MobCoinsRepositories.CACHE_LOCAL.fetch(p.getName());

        if(context.getArgs().length == 0) {
            p.sendMessage("§eSuas mobcoins: §f" + user.getMobcoins());
            return;
        }

        if(context.getArg(0).equalsIgnoreCase("loja")) {
            MobCoinsPlugin.viewFrame.open(MobCoinsShopView.class, p, ImmutableMap.of("user", user));
            return;
        }

        if(context.getArg(0).equalsIgnoreCase("adicionar")) {
            if(p.hasPermission(MobCoinsPlugin.plugin.getConfig().getString("Settings.AdminPermission"))) {

                Player target;
                double amount;

                try {
                    target = Bukkit.getPlayerExact(context.getArg(1));
                    amount = Double.parseDouble(context.getArg(2));
                } catch (Throwable e) {
                    p.sendMessage("§cUtilize, /mobcoins adicionar <jogador< quantia>.");
                    return;
                }

                if (target == null) {
                    p.sendMessage("§cO jogador alvo esta offline ou não existe.");
                    return;
                }

                User targetCache = MobCoinsRepositories.CACHE_LOCAL.fetch(target.getName());

                targetCache.setMobcoins(targetCache.getMobcoins() + amount);
                Schedulers.async().runLater(() -> MobCoinsRepositories.MYSQL.insert(targetCache),  5, TimeUnit.SECONDS);
                p.sendMessage("§eVocê adicionou §f" + NumberFormat.getInstance().format(amount) + " §eMobCoins para o jogador §f" + target.getName() + "§e.");
                return;
            }else{
                p.sendMessage("§cSem permissão.");
                return;
            }
        }

        if(context.getArg(0).equalsIgnoreCase("remover")) {
            if(p.hasPermission(MobCoinsPlugin.plugin.getConfig().getString("Settings.AdminPermission"))) {

                Player target;
                double amount;

                try {
                    target = Bukkit.getPlayerExact(context.getArg(1));
                    amount = Double.parseDouble(context.getArg(2));
                } catch (Throwable e) {
                    p.sendMessage("§cUtilize, /mobcoins remover <jogador< quantia>.");
                    return;
                }

                if (target == null) {
                    p.sendMessage("§cO jogador alvo esta offline ou não existe.");
                    return;
                }

                User targetCache = MobCoinsRepositories.CACHE_LOCAL.fetch(target.getName());

                if(amount > targetCache.getMobcoins()) {
                    targetCache.setMobcoins(0);
                    Schedulers.async().runLater(() -> MobCoinsRepositories.MYSQL.insert(targetCache),  5, TimeUnit.SECONDS);
                    p.sendMessage("§eVocê removeu §f" + NumberFormat.getInstance().format(amount) + " §eMobCoins do jogador §f" + target.getName() + "§e.");
                    return;
                }

                targetCache.setMobcoins(targetCache.getMobcoins() - amount);
                Schedulers.async().runLater(() -> MobCoinsRepositories.MYSQL.insert(targetCache),  5, TimeUnit.SECONDS);
                p.sendMessage("§eVocê removeu §f" + NumberFormat.getInstance().format(amount) + " §eMobCoins do jogador §f" + target.getName() + "§e.");
                return;
            }else{
                p.sendMessage("§cSem permissão.");
                return;
            }
        }
    }
}
