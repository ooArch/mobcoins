package com.github.merelysnow.mobcoins.model.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Data
public class Store {

    private ItemStack icon;
    private String name;
    private int slot;
    private double price;
    private String command;
}
