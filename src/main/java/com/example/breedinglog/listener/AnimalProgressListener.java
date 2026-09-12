package com.example.breedinglog.listener;

import com.example.breedinglog.BreedingLogPlugin;
import com.example.breedinglog.animal.AnimalRegistry;
import com.example.breedinglog.data.AnimalData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public final class AnimalProgressListener implements Listener {
    private final BreedingLogPlugin plugin;

    public AnimalProgressListener(BreedingLogPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAnimalDamaged(EntityDamageByEntityEvent event) {
        Entity animal = event.getEntity();
        if (!AnimalRegistry.isSupported(animal) || !AnimalData.isAdopted(animal, plugin.pdcKeys())) {
            return;
        }
        int decrease = plugin.getConfig().getInt("affection.attack-decrease", 10);
        int affection = AnimalData.changeAffection(animal, plugin.pdcKeys(), -decrease,
                plugin.getConfig().getInt("affection.minimum", 0),
                plugin.getConfig().getInt("affection.maximum", 100));
        if (event.getDamager() instanceof Player player
                && player.getUniqueId().equals(AnimalData.owner(animal, plugin.pdcKeys()))) {
            player.sendMessage(plugin.languageService().message(player, "hurt-animal", affection));
        }
    }

    @EventHandler
    public void onAnimalDeath(EntityDeathEvent event) {
        Entity animal = event.getEntity();
        if (!AnimalRegistry.isSupported(animal) || event.getEntity().getKiller() == null) {
            return;
        }
        Player player = event.getEntity().getKiller();
        plugin.ranchService().addExperience(player,
                plugin.getConfig().getInt("ranch.kill-experience", 10));
    }
}