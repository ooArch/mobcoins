package com.github.merelysnow.mobcoins.model.store;

import com.github.merelysnow.mobcoins.utils.DateManager;
import com.github.merelysnow.mobcoins.utils.ItemBuilder;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StoreDAO {

    private static Map<String, Store> itemMap;

    private FileConfiguration config;

    public StoreDAO(FileConfiguration config) {
        itemMap = new HashMap<>();
        this.config = config;

        setupItem();
    }

    public void createItem(String path, Store item) {
        itemMap.put(path, item);
    }

    public Map<String, Store> getItem() {
        return itemMap;
    }

    private void setupItem() {
        for (String path : DateManager.getConfig("loja").getConfigurationSection("Loja").getKeys(false)) {
            ConfigurationSection key = DateManager.getConfig("loja").getConfigurationSection("Loja." + path);

            val material = Integer.parseInt(key.getString("Material").split(":")[0]);
            val data = Integer.parseInt(key.getString("Material").split(":")[1]);
            val slot = key.getInt("Slot");
            val name = key.getString("Name").replace("&", "§");
            val price = key.getInt("Price");
            val cmd = key.getString("Command");
            List<String> lore = key.getStringList("Lore");
            lore = lore.stream().map(l -> l.replace("&", "§")
                    .replace("{price}", "" + NumberFormat.getInstance().format(price))).collect(Collectors.toList());

            val item = new Store(new ItemBuilder(Material.getMaterial(material), 1, (short)data)
                    .setName(name)
                    .setLore(lore).build()
                    , name, slot, price, cmd);
            createItem(path, item);

        }
    }
}
