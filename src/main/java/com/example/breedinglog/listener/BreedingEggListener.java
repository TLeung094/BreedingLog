package com.example.breedinglog.listener;

import com.example.breedinglog.BreedingLogPlugin;
import com.example.breedinglog.animal.Gender;
import com.example.breedinglog.data.AnimalData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class BreedingEggListener implements Listener {
    private final BreedingLogPlugin plugin;

    public BreedingEggListener(BreedingLogPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEggUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack item = event.getItem();
        if (!isBreedingEgg(item)) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        EntityType species = species(item);
        if (species == null) {
            player.sendMessage(plugin.languageService().message(player, "egg-invalid"));
            return;
        }
        EquipmentSlot hand = event.getHand();
        plugin.scheduler().runAtLocation(player.getLocation(), () -> hatch(player, item, species, hand));
    }

    private void hatch(Player player, ItemStack item, EntityType species, EquipmentSlot hand) {
        Entity baby = player.getWorld().spawnEntity(player.getLocation(), species);
        if (baby instanceof Ageable ageable) {
            ageable.setBaby();
        }
        AnimalData.adopt(baby, plugin.pdcKeys(), player.getUniqueId(), Gender.random(),
                plugin.getConfig().getInt("affection.initial", 50));
        plugin.animalNameService().update(baby);
        consume(item, player, hand);
        plugin.ranchService().addExperience(player,
                plugin.getConfig().getInt("ranch.birth-experience", 50));
        player.sendMessage(plugin.languageService().message(player, "egg-hatched"));
    }

    private boolean isBreedingEgg(ItemStack item) {
        if (item == null || item.getType() != Material.EGG || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(plugin.pdcKeys().breedingEgg(), PersistentDataType.BYTE);
    }

    private EntityType species(ItemStack item) {
        String value = item.getItemMeta().getPersistentDataContainer()
                .get(plugin.pdcKeys().eggSpecies(), PersistentDataType.STRING);
        if (value == null) {
            return null;
        }
        try {
            return EntityType.valueOf(value);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private void consume(ItemStack item, Player player, EquipmentSlot hand) {
        if (item.getAmount() <= 1) {
            if (hand == EquipmentSlot.OFF_HAND) {
                player.getInventory().setItemInOffHand(null);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
        } else {
            item.setAmount(item.getAmount() - 1);
        }
    }
}