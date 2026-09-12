package com.example.breedinglog.data;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public final class PdcKeys {
    private final NamespacedKey ownerUuid;
    private final NamespacedKey adopted;
    private final NamespacedKey gender;
    private final NamespacedKey affection;
    private final NamespacedKey ranchLevel;
    private final NamespacedKey ranchExperience;
    private final NamespacedKey displayEnabled;
    private final NamespacedKey pregnancyEnd;
    private final NamespacedKey language;
    private final NamespacedKey breedingEgg;
    private final NamespacedKey eggSpecies;

    public PdcKeys(Plugin plugin) {
        ownerUuid = new NamespacedKey(plugin, "owner_uuid");
        adopted = new NamespacedKey(plugin, "adopted");
        gender = new NamespacedKey(plugin, "gender");
        affection = new NamespacedKey(plugin, "affection");
        ranchLevel = new NamespacedKey(plugin, "ranch_level");
        ranchExperience = new NamespacedKey(plugin, "ranch_experience");
        displayEnabled = new NamespacedKey(plugin, "display_enabled");
        pregnancyEnd = new NamespacedKey(plugin, "pregnancy_end");
        language = new NamespacedKey(plugin, "language");
        breedingEgg = new NamespacedKey(plugin, "breeding_egg");
        eggSpecies = new NamespacedKey(plugin, "egg_species");
    }

    public NamespacedKey ownerUuid() {
        return ownerUuid;
    }

    public NamespacedKey adopted() {
        return adopted;
    }

    public NamespacedKey gender() {
        return gender;
    }

    public NamespacedKey affection() {
        return affection;
    }

    public NamespacedKey ranchLevel() {
        return ranchLevel;
    }

    public NamespacedKey ranchExperience() {
        return ranchExperience;
    }

    public NamespacedKey displayEnabled() {
        return displayEnabled;
    }

    public NamespacedKey pregnancyEnd() {
        return pregnancyEnd;
    }

    public NamespacedKey language() {
        return language;
    }

    public NamespacedKey breedingEgg() {
        return breedingEgg;
    }

    public NamespacedKey eggSpecies() {
        return eggSpecies;
    }
}
