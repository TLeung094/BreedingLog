package com.example.breedinglog.animal;

import com.example.breedinglog.BreedingLogPlugin;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public final class RanchService {
    private final BreedingLogPlugin plugin;

    public RanchService(BreedingLogPlugin plugin) {
        this.plugin = plugin;
    }

    public void addExperience(Player player, int amount) {
        if (amount <= 0) {
            return;
        }
        var data = player.getPersistentDataContainer();
        int level = data.getOrDefault(plugin.pdcKeys().ranchLevel(), PersistentDataType.INTEGER, 1);
        int experience = data.getOrDefault(plugin.pdcKeys().ranchExperience(), PersistentDataType.INTEGER, 0);
        int nextExperience = experience + amount;
        int levelsGained = 0;
        while (nextExperience >= experienceRequired(level)) {
            nextExperience -= experienceRequired(level);
            level++;
            levelsGained++;
        }
        data.set(plugin.pdcKeys().ranchLevel(), PersistentDataType.INTEGER, level);
        data.set(plugin.pdcKeys().ranchExperience(), PersistentDataType.INTEGER, nextExperience);
        if (levelsGained > 0) {
            player.sendMessage(plugin.languageService().message(player, "level-up", level));
        }
    }

    public int experienceRequired(int level) {
        int base = plugin.getConfig().getInt("ranch.experience-per-level", 100);
        int increment = plugin.getConfig().getInt("ranch.experience-level-increment", 50);
        return Math.max(1, base + Math.max(0, level - 1) * increment);
    }

    public UUID owner(Player player) {
        return player.getUniqueId();
    }
}