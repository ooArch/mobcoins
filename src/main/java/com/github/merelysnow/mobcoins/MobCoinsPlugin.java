package com.github.merelysnow.mobcoins;

import com.github.merelysnow.mobcoins.commands.MobCoinsCommand;
import com.github.merelysnow.mobcoins.controller.EntityController;
import com.github.merelysnow.mobcoins.controller.UserController;
import com.github.merelysnow.mobcoins.database.MobCoinsDatabase;
import com.github.merelysnow.mobcoins.database.connection.RepositoryProvider;
import com.github.merelysnow.mobcoins.listeners.PlayerListener;
import com.github.merelysnow.mobcoins.model.store.StoreDAO;
import com.github.merelysnow.mobcoins.thread.HologramThread;
import com.github.merelysnow.mobcoins.utils.DateManager;
import com.github.merelysnow.mobcoins.view.MobCoinsShopView;
import com.google.common.primitives.Doubles;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import lombok.Getter;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import me.saiintbrisson.minecraft.ViewFrame;
import me.saiintbrisson.minecraft.command.message.MessageHolder;
import me.saiintbrisson.minecraft.command.message.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;

@Getter
public class MobCoinsPlugin extends JavaPlugin {
    private ViewFrame viewFrame;
    private StoreDAO storeDAO;
    private MobCoinsDatabase mobCoinsDatabase;
    private UserController userController;
    private EntityController entityController;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        DateManager.createConfig("loja");

        mobCoinsDatabase = new MobCoinsDatabase(this);
        userController = new UserController(getMobCoinsDatabase());
        entityController = new EntityController();

        entityController.loadConfiguration();

        storeDAO = new StoreDAO(DateManager.getConfig("loja"));
        viewFrame = ViewFrame.of(this, new MobCoinsShopView()).register();

        mobCoinsDatabase.selectMany()
                .forEach(user -> {
                    userController.registerUser(user);
                });

        registerCommands();
        registerEvents();

        if (getConfig().contains("Locais.Holograma")) {
            (new HologramThread()).runTaskTimer(this, 20L, 60 * 30 * 20L);
            Bukkit.getConsoleSender().sendMessage("§6[MobCoins] §eHolograma registrado.");
        }

        Bukkit.getConsoleSender().sendMessage("§6[MobCoins] §ePlugin iniciado");
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    private void registerCommands() {
        BukkitFrame bukkitFrame = new BukkitFrame(this);

        bukkitFrame.registerCommands(
                new MobCoinsCommand()
        );

        MessageHolder messageHolder = bukkitFrame.getMessageHolder();

        messageHolder.setMessage(MessageType.NO_PERMISSION, "§cVocê não tem permissão para executar este comando.");
        messageHolder.setMessage(MessageType.ERROR, "§cUm erro ocorreu! {error}");
        messageHolder.setMessage(MessageType.INCORRECT_USAGE, "§cUtilize /{usage}");
        messageHolder.setMessage(MessageType.INCORRECT_TARGET, "§cVocê não pode utilizar este comando pois ele é direcioado apenas para {target}.");
    }


    public static MobCoinsPlugin getInstance() {
        return getPlugin(MobCoinsPlugin.class);
    }
}
