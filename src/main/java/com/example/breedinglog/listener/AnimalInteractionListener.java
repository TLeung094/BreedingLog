package com.example.breedinglog.listener;

import com.example.breedinglog.BreedingLogPlugin;
import com.example.breedinglog.animal.AnimalRegistry;
import com.example.breedinglog.animal.BreedingService;
import com.example.breedinglog.animal.Gender;
import com.example.breedinglog.animal.HarvestService;
import com.example.breedinglog.data.AnimalData;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public final class AnimalInteractionListener implements Listener {
    private final BreedingLogPlugin plugin;
    private final ConcurrentMap<UUID, Long> patCooldowns = new ConcurrentHashMap<>();
    private final BreedingService breedingService;
    private final HarvestService harvestService;
    private final AnimalStatusGui statusGui;

    public AnimalInteractionListener(BreedingLogPlugin plugin) {
        this.plugin = plugin;
        this.breedingService = new BreedingService(plugin);
        this.harvestService = new HarvestService(plugin);
        this.statusGui = new AnimalStatusGui(plugin);
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        org.bukkit.inventory.ItemStack heldItem = event.getHand() == org.bukkit.inventory.EquipmentSlot.OFF_HAND
            ? player.getInventory().getItemInOffHand()
            : player.getInventory().getItemInMainHand();
        if (harvestService.tryHarvest(player, entity, heldItem, event.getHand())) {
            event.setCancelled(true);
            return;
        }
        if (breedingService.isBreedingFood(entity, player.getInventory().getItemInMainHand())) {
            event.setCancelled(true);
            plugin.scheduler().runAtEntity(entity, () -> breedingService.tryBreed(player, entity));
            return;
        }
        if (event.getHand() == org.bukkit.inventory.EquipmentSlot.HAND
                && !player.isSneaking() && heldItem.isEmpty() && AnimalRegistry.isSupported(entity)) {
            event.setCancelled(true);
            plugin.scheduler().runAtEntity(entity, () -> statusGui.open(player, entity));
            return;
        }
        if (!player.isSneaking() || !player.getInventory().getItemInMainHand().isEmpty()
                || !AnimalRegistry.isSupported(entity)) {
            return;
        }

        event.setCancelled(true);
        plugin.scheduler().runAtEntity(entity, () -> handle(player, entity));
    }

    private void handle(Player player, Entity entity) {
        if (!AnimalData.isAdopted(entity, plugin.pdcKeys())) {
            int initial = plugin.getConfig().getInt("affection.initial", 50);
            AnimalData.adopt(entity, plugin.pdcKeys(), player.getUniqueId(), Gender.random(), initial);
            plugin.animalNameService().update(entity);
            player.sendMessage(plugin.languageService().message(player, "adopted"));
            showStatus(player, entity);
            return;
        }

        UUID owner = AnimalData.owner(entity, plugin.pdcKeys());
        if (!player.getUniqueId().equals(owner)) {
            player.sendMessage(plugin.languageService().message(player, "owned"));
            return;
        }

        long now = System.currentTimeMillis();
        long cooldown = plugin.getConfig().getLong("affection.pat-cooldown-seconds", 30) * 1000L;
        Long lastPat = patCooldowns.get(entity.getUniqueId());
        if (lastPat != null && now - lastPat < cooldown) {
            player.sendMessage(plugin.languageService().message(player, "pat-cooldown"));
            showStatus(player, entity);
            return;
        }

        patCooldowns.put(entity.getUniqueId(), now);
        int affection = AnimalData.changeAffection(entity, plugin.pdcKeys(),
                plugin.getConfig().getInt("affection.pat-increase", 5),
                plugin.getConfig().getInt("affection.minimum", 0),
                plugin.getConfig().getInt("affection.maximum", 100));
        player.sendMessage(plugin.languageService().message(player, "pat-success", affection));
        showStatus(player, entity);
    }

    private void showStatus(Player player, Entity entity) {
        byte displayEnabled = player.getPersistentDataContainer().getOrDefault(
                plugin.pdcKeys().displayEnabled(), org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);
        if (displayEnabled == 0) {
            return;
        }
        Gender gender = AnimalData.gender(entity, plugin.pdcKeys());
        int affection = AnimalData.affection(entity, plugin.pdcKeys(), 50);
        String genderText = gender == null ? "?" : gender.symbol();
        player.sendActionBar(ChatColor.YELLOW + entity.getType().name() + " " + genderText
                + ChatColor.GRAY + " | " + ChatColor.RED + "❤ " + affection);
    }
}
