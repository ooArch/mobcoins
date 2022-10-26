package com.github.merelysnow.mobcoins.commands;

import com.github.merelysnow.mobcoins.MobCoinsPlugin;
import com.github.merelysnow.mobcoins.MobCoinsRepositories;
import com.github.merelysnow.mobcoins.model.User;
import com.github.merelysnow.mobcoins.utils.ActionBar;
import com.github.merelysnow.mobcoins.utils.StringUtils;
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
    public void handleCommand(Context<Player> context) {

        val p = context.getSender();
        User user = MobCoinsRepositories.CACHE_LOCAL.fetch(p.getName());

        p.sendMessage("§eSuas mobcoins: §f" + NumberFormat.getInstance().format(user.getMobcoins()));
        return;
    }

    @Command(
            name = "mobcoins.loja",
            usage = "mobcoins loja",
            target = CommandTarget.PLAYER
    )
    public void subCommandLoja(Context<Player> context) {

        val p = context.getSender();
        User user = MobCoinsRepositories.CACHE_LOCAL.fetch(p.getName());

        MobCoinsPlugin.viewFrame.open(MobCoinsShopView.class, p, ImmutableMap.of("user", user));
        return;
    }

    @Command(
            name = "mobcoins.adicionar",
            usage = "mobcoins adicionar <player> <quantia>",
            target = CommandTarget.PLAYER,
            permission = "mobcoins.admin"
    )
    public void subCommandAdicionar(Context<Player> context, Player target, Double amount) {

        val p = context.getSender();
        User targetCache = MobCoinsRepositories.CACHE_LOCAL.fetch(target.getName());

        if (targetCache == null) {
            p.sendMessage("§cO jogador alvo não foi encontado em nosso Cache.");
            return;
        }

        targetCache.setMobcoins(targetCache.getMobcoins() + amount);
        Schedulers.async().runLater(() -> MobCoinsRepositories.MYSQL.insert(targetCache), 5, TimeUnit.SECONDS);
        p.sendMessage("§eVocê adicionou §f" + NumberFormat.getInstance().format(amount) + " §eMobCoins para o jogador §f" + target.getName() + "§e.");
        return;
    }

    @Command(
            name = "mobcoins.remover",
            usage = "mobcoins remover <player> <quantia>",
            target = CommandTarget.PLAYER,
            permission = "mobcoins.admin"
    )
    public void subCommandRemover(Context<Player> context, Player target, Double amount) {

        val p = context.getSender();
        User targetCache = MobCoinsRepositories.CACHE_LOCAL.fetch(target.getName());

        if (targetCache == null) {
            p.sendMessage("§cO jogador alvo não foi encontado em nosso Cache.");
            return;
        }

        if (amount > targetCache.getMobcoins()) {
            targetCache.setMobcoins(0);
            Schedulers.async().runLater(() -> MobCoinsRepositories.MYSQL.insert(targetCache), 5, TimeUnit.SECONDS);
            p.sendMessage("§eVocê removeu §f" + NumberFormat.getInstance().format(amount) + " §eMobCoins do jogador §f" + target.getName() + "§e.");
            return;
        }

        targetCache.setMobcoins(targetCache.getMobcoins() - amount);
        Schedulers.async().runLater(() -> MobCoinsRepositories.MYSQL.insert(targetCache), 5, TimeUnit.SECONDS);
        p.sendMessage("§eVocê removeu §f" + NumberFormat.getInstance().format(amount) + " §eMobCoins do jogador §f" + target.getName() + "§e.");
        return;
    }

    @Command(
            name = "mobcoins.setar",
            usage = "mobcoins setar <tipo>",
            permission = "mobcoins.admin",
            target = CommandTarget.PLAYER
    )
    public void subCommandSetar(Context<Player> context, String type) {

        val p = context.getSender();

        switch (type) {
            case "holograma":
                MobCoinsPlugin.plugin.getConfig().set("Locais.Holograma", StringUtils.serializeLocation(p.getLocation()));
                MobCoinsPlugin.plugin.saveConfig();
                ActionBar.sendToPlayer(p,"§eLocal 'hologram' setado com sucesso");
                break;

            default:
                ActionBar.sendToPlayer(p, "§cTipo invalido. (holograma)");
                break;
        }
    }
}
