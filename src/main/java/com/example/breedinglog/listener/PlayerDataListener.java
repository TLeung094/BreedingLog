package com.example.breedinglog.listener;

import com.example.breedinglog.BreedingLogPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

public final class PlayerDataListener implements Listener {
    private final BreedingLogPlugin plugin;

    public PlayerDataListener(BreedingLogPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var data = event.getPlayer().getPersistentDataContainer();
        if (!data.has(plugin.pdcKeys().ranchLevel(), PersistentDataType.INTEGER)) {
            data.set(plugin.pdcKeys().ranchLevel(), PersistentDataType.INTEGER, 1);
        }
        if (!data.has(plugin.pdcKeys().ranchExperience(), PersistentDataType.INTEGER)) {
            data.set(plugin.pdcKeys().ranchExperience(), PersistentDataType.INTEGER, 0);
        }
        if (!data.has(plugin.pdcKeys().displayEnabled(), PersistentDataType.BYTE)) {
            data.set(plugin.pdcKeys().displayEnabled(), PersistentDataType.BYTE, (byte) 1);
        }
    }
}