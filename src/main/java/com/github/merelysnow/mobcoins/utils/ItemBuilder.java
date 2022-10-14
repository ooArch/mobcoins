package com.github.merelysnow.mobcoins.utils;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ItemBuilder
{
    private ItemStack item;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public ItemBuilder(Material material, int amount) {
        item = new ItemStack(material, amount);
    }

    public ItemBuilder(Material material, int amount, int data) {
        item = new ItemStack(material, amount, (short) data);
    }

    public ItemBuilder(String url) {
        item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

        if (url == null || url.isEmpty())
            return;

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        if (url.equalsIgnoreCase("PLAYER_HEAD".toUpperCase())) {
            skullMeta.setOwner(url); /* Insert player name in "url" */
            item.setItemMeta(skullMeta);
            return;

        }
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", new Object[]{url}).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;

        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");

        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        assert profileField != null;
        profileField.setAccessible(true);

        try {
            profileField.set(skullMeta, profile);

        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        item.setItemMeta(skullMeta);
    }

    public ItemBuilder clone() {
        return new ItemBuilder(item);
    }

    public ItemBuilder setDurability(short durability) {
        item.setDurability(durability);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_POTION_EFFECTS});
        item.setItemMeta(meta);
        item.setAmount(amount);

        return this;
    }

    public ItemBuilder setDurability(String durability) {
        item.setDurability(Short.valueOf(durability).shortValue());
        return this;
    }

    public ItemBuilder setName(String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name.replace("&", "§"));
        item.setItemMeta(meta);

        return this;
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
        item.addUnsafeEnchantment(ench, level);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment ench) {
        item.removeEnchantment(ench);
        return this;
    }

    public ItemBuilder setSkullOwner(String dono) {
        try {
            SkullMeta im = (SkullMeta) item.getItemMeta();
            im.setOwner(dono);
            item.setItemMeta((ItemMeta) im);

        } catch (ClassCastException classCastException) {

        }
        return this;
    }

    public ItemBuilder addEnchant(Enchantment ench, int level) {
        ItemMeta im = item.getItemMeta();
        im.addEnchant(ench, level, true);

        item.setItemMeta(im);
        return this;
    }

    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        item.addEnchantments(enchantments);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        lore = lore.stream().map(l -> l.replace("&", "§")).collect(Collectors.toList());

        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);

        return this;
    }

    public ItemBuilder addItemFlag(final ItemFlag flag) {
        final ItemMeta im = item.getItemMeta();
        im.addItemFlags(flag);
        item.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeLoreLine(String line) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>(meta.getLore());

        if (!lore.contains(line))
            return this;

        lore.remove(line);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return this;
    }

    public ItemBuilder removeLoreLine(int index) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>(meta.getLore());

        if (index < 0 || index > lore.size())
            return this;

        lore.remove(index);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        if (meta.hasLore())
            lore = new ArrayList<>(meta.getLore());

        lore.add(line);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return this;
    }

    public ItemBuilder addLores(List<String> line) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        if (meta.hasLore())
            lore = new ArrayList<>(meta.getLore());

        for (String s : line)
            lore.add(s);

        meta.setLore(lore);
        item.setItemMeta(meta);

        return this;
    }

    public ItemBuilder addLoreLine(String line, int position) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>(meta.getLore());

        lore.set(position, line);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return this;
    }

    public ItemBuilder owner(String owner) {
        try {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwner(owner);
            item.setItemMeta(meta);

        } catch (ClassCastException ignored) {
        }
        return this;
    }

    public ItemBuilder color(Color color) {
        if (!item.getType().name().contains("LEATHER_"))
            return this;

        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);

        return this;
    }

    public ItemBuilder setDyeColor(DyeColor color) {
        item.setDurability(color.getData());
        return this;
    }

    @Deprecated
    public ItemBuilder setWoolColor(DyeColor color) {
        if (!item.getType().equals(Material.WOOL))
            return this;

        item.setDurability(color.getData());
        return this;
    }

    public ItemBuilder setLeatherArmorColor(Color color) {
        try {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(color);
            item.setItemMeta(meta);

        } catch (ClassCastException ignored) {
        }
        return this;
    }

    public ItemBuilder builder(Consumer<ItemStack> consumer) {
        consumer.accept(item);
        return this;
    }

    public ItemBuilder builderMeta(Consumer<ItemMeta> consumer) {
        ItemMeta meta = item.getItemMeta();
        consumer.accept(meta);
        item.setItemMeta(meta);

        return this;
    }

    public ItemBuilder addGlow(boolean glow) {
        if (!glow)
            return this;

        builder(it -> it.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1));
        builderMeta(meta -> meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS}));
        return this;
    }

    public ItemStack build() {
        return item;
    }
}
