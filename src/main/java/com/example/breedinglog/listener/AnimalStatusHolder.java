package com.example.breedinglog.listener;

import java.util.UUID;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class AnimalStatusHolder implements InventoryHolder {
    private final UUID animalId;
    private Inventory inventory;

    public AnimalStatusHolder(UUID animalId) {
        this.animalId = animalId;
    }

    public UUID animalId() {
        return animalId;
    }

    public void inventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}