package com.example.breedinglog.animal;

import com.example.breedinglog.BreedingLogPlugin;
import com.example.breedinglog.data.AnimalData;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public final class AffectionDecayService {
    private static final long DAY_MILLIS = 86_400_000L;
    private final BreedingLogPlugin plugin;

    public AffectionDecayService(BreedingLogPlugin plugin) {
        this.plugin = plugin;
    }

    public void decayLoadedAnimals() {
        long now = System.currentTimeMillis();
        for (World world : plugin.getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!AnimalRegistry.isSupported(entity) || !AnimalData.isAdopted(entity, plugin.pdcKeys())) {
                    continue;
                }
                plugin.scheduler().runAtEntity(entity, () -> decay(entity, now));
            }
        }
    }

    private void decay(Entity entity, long now) {
        int amount = plugin.getConfig().getInt("affection.daily-decay", 1);
        int floor = plugin.getConfig().getInt("affection.decay-floor", 50);
        int minimum = plugin.getConfig().getInt("affection.minimum", 0);
        int current = AnimalData.affection(entity, plugin.pdcKeys(), 50);
        if (current <= floor) {
            return;
        }
        AnimalData.changeAffection(entity, plugin.pdcKeys(), -amount, Math.max(minimum, floor),
                plugin.getConfig().getInt("affection.maximum", 100));
    }
}