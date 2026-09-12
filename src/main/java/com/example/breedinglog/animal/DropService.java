package com.example.breedinglog.animal;

import com.example.breedinglog.BreedingLogPlugin;
import java.io.File;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

public final class DropService {
    private final BreedingLogPlugin plugin;
    private FileConfiguration drops;

    public DropService(BreedingLogPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "drops.yml");
        if (!file.exists()) {
            plugin.saveResource("drops.yml", false);
        }
        drops = YamlConfiguration.loadConfiguration(file);
    }

    public Material tool(EntityType type) {
        String value = drops.getString("harvest." + type.name() + ".tool");
        return value == null ? null : Material.matchMaterial(value);
    }

    public Material result(EntityType type) {
        String value = drops.getString("harvest." + type.name() + ".result");
        return value == null ? null : Material.matchMaterial(value);
    }

    public int amount(EntityType type) {
        return Math.max(1, drops.getInt("harvest." + type.name() + ".amount", 1));
    }

    public boolean hasPassiveDrop(EntityType type) {
        return drops.getBoolean("passive.types." + type.name(), false);
    }

    public double passiveChance() {
        return Math.clamp(drops.getDouble("passive.chance", 1.0), 0.0, 1.0);
    }

    public int passiveIntervalTicks() {
        return Math.max(20, drops.getInt("passive.interval-seconds", 300) * 20);
    }

    public Material passiveResult() {
        String value = drops.getString("passive.result");
        return value == null ? null : Material.matchMaterial(value);
    }

    public int passiveAmount() {
        return Math.max(1, drops.getInt("passive.amount", 1));
    }
}