package com.example.breedinglog.animal;

import com.example.breedinglog.BreedingLogPlugin;
import com.example.breedinglog.data.AnimalData;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public final class PassiveDropService {
    private final BreedingLogPlugin plugin;

    public PassiveDropService(BreedingLogPlugin plugin) {
        this.plugin = plugin;
    }

    public void dropFromLoadedAnimals() {
        for (World world : plugin.getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!AnimalRegistry.isSupported(entity)
                        || !AnimalData.isAdopted(entity, plugin.pdcKeys())
                        || !plugin.dropService().hasPassiveDrop(entity.getType())) {
                    continue;
                }
                plugin.scheduler().runAtEntity(entity, () -> drop(entity));
            }
        }
    }

    private void drop(Entity entity) {
        if (!entity.isValid() || ThreadLocalRandom.current().nextDouble() > plugin.dropService().passiveChance()) {
            return;
        }
        var result = plugin.dropService().passiveResult();
        if (result == null) {
            return;
        }
        entity.getWorld().dropItemNaturally(entity.getLocation(),
                new ItemStack(result, plugin.dropService().passiveAmount()));
    }
}