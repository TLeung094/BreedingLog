package com.example.breedinglog.animal;

import com.example.breedinglog.BreedingLogPlugin;
import com.example.breedinglog.data.AnimalData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

public final class AnimalNameService {
    private final BreedingLogPlugin plugin;

    public AnimalNameService(BreedingLogPlugin plugin) {
        this.plugin = plugin;
    }

    public void update(Entity animal) {
        if (!AnimalData.isAdopted(animal, plugin.pdcKeys())) {
            return;
        }
        Gender gender = AnimalData.gender(animal, plugin.pdcKeys());
        if (gender == null) {
            return;
        }
        animal.setCustomName(ChatColor.YELLOW + animal.getType().name() + " " + gender.symbol());
        animal.setCustomNameVisible(true);
    }
}