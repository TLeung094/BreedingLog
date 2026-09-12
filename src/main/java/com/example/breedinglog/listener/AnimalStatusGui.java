package com.example.breedinglog.listener;

import com.example.breedinglog.BreedingLogPlugin;
import com.example.breedinglog.animal.Gender;
import com.example.breedinglog.data.AnimalData;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class AnimalStatusGui {
    private final BreedingLogPlugin plugin;

    public AnimalStatusGui(BreedingLogPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, Entity animal) {
        AnimalStatusHolder holder = new AnimalStatusHolder(animal.getUniqueId());
        Inventory inventory = plugin.getServer().createInventory(holder, 27,
                plugin.languageService().message(player, "status-title"));
        holder.inventory(inventory);
        ItemStack filler = item(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, filler);
        }
        inventory.setItem(10, item(Material.NAME_TAG,
                plugin.languageService().message(player, "status-species", animal.getType().name())));
        Gender gender = AnimalData.gender(animal, plugin.pdcKeys());
        inventory.setItem(12, item(Material.CLOCK,
                plugin.languageService().message(player, "status-gender",
                        gender == null ? "?" : gender.symbol())));
        inventory.setItem(14, item(Material.RED_DYE,
                plugin.languageService().message(player, "status-affection",
                        AnimalData.affection(animal, plugin.pdcKeys(), 50))));
        inventory.setItem(16, item(Material.CHEST,
                plugin.languageService().message(player, "status-owner", ownerName(animal))));
        inventory.setItem(22, item(Material.EGG,
                plugin.languageService().message(player, "status-pregnancy",
                        pregnancyStatus(animal))));
        player.openInventory(inventory);
    }

    private String ownerName(Entity animal) {
        UUID owner = AnimalData.owner(animal, plugin.pdcKeys());
        if (owner == null) {
            return plugin.languageService().message(null, "status-wild");
        }
        String name = plugin.getServer().getOfflinePlayer(owner).getName();
        return name == null ? owner.toString().substring(0, 8) : name;
    }

    private String pregnancyStatus(Entity animal) {
        if (!AnimalData.isPregnant(animal, plugin.pdcKeys())) {
            return plugin.languageService().message(null, "status-not-pregnant");
        }
        Long end = animal.getPersistentDataContainer().get(
                plugin.pdcKeys().pregnancyEnd(), org.bukkit.persistence.PersistentDataType.LONG);
        long remaining = Math.max(0L, (end - System.currentTimeMillis()) / 1000L);
        return remaining + "s";
    }

    private ItemStack item(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + name);
        item.setItemMeta(meta);
        return item;
    }
}