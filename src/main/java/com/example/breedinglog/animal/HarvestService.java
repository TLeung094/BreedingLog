package com.example.breedinglog.animal;

import com.example.breedinglog.BreedingLogPlugin;
import com.example.breedinglog.data.AnimalData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class HarvestService {
    private final BreedingLogPlugin plugin;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    public HarvestService(BreedingLogPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean tryHarvest(Player player, Entity animal, ItemStack tool, EquipmentSlot hand) {
        if (!AnimalRegistry.isSupported(animal) || !AnimalData.isAdopted(animal, plugin.pdcKeys())
                || !player.getUniqueId().equals(AnimalData.owner(animal, plugin.pdcKeys())) || tool == null) {
            return false;
        }
        Material result = plugin.dropService().result(animal.getType());
        Material configuredTool = plugin.dropService().tool(animal.getType());
        if (result == null || configuredTool == null || tool.getType() != configuredTool) {
            return false;
        }
        String messageKey;
        if (animal instanceof Sheep sheep && configuredTool == Material.SHEARS && !sheep.isSheared()) {
            sheep.setSheared(true);
            messageKey = "harvest-wool";
        } else if ((animal.getType() == org.bukkit.entity.EntityType.COW
                || animal.getType() == org.bukkit.entity.EntityType.GOAT)
                && configuredTool == Material.BUCKET) {
            messageKey = "harvest-milk";
        } else if (animal instanceof MushroomCow && configuredTool == Material.BOWL) {
            messageKey = "harvest-stew";
        } else if (configuredTool == Material.BRUSH
            && (animal.getType() == org.bukkit.entity.EntityType.CHICKEN
            || animal.getType() == org.bukkit.entity.EntityType.ARMADILLO)) {
            long now = System.currentTimeMillis();
            long cooldown = plugin.getConfig().getLong("affection.harvest-cooldown-seconds", 30) * 1000L;
            Long availableAt = cooldowns.get(animal.getUniqueId());
            if (availableAt != null && now < availableAt) {
                return false;
            }
            cooldowns.put(animal.getUniqueId(), now + cooldown);
                messageKey = animal.getType() == org.bukkit.entity.EntityType.ARMADILLO
                    ? "harvest-scute" : "harvest-egg";
            } else if (configuredTool == Material.SHEARS
                && (animal.getType() == org.bukkit.entity.EntityType.RABBIT
                || animal.getType() == org.bukkit.entity.EntityType.BEE)) {
                long now = System.currentTimeMillis();
                long cooldown = plugin.getConfig().getLong("affection.harvest-cooldown-seconds", 30) * 1000L;
                Long availableAt = cooldowns.get(animal.getUniqueId());
                if (availableAt != null && now < availableAt) {
                return false;
                }
                cooldowns.put(animal.getUniqueId(), now + cooldown);
                messageKey = animal.getType() == org.bukkit.entity.EntityType.BEE
                    ? "harvest-honeycomb" : "harvest-hide";
        } else {
            return false;
        }

        replaceHand(player, hand, new ItemStack(result, plugin.dropService().amount(animal.getType())));
        AnimalData.changeAffection(animal, plugin.pdcKeys(),
                -plugin.getConfig().getInt("affection.frequent-harvest-decrease", 3),
                plugin.getConfig().getInt("affection.minimum", 0),
                plugin.getConfig().getInt("affection.maximum", 100));
        plugin.ranchService().addExperience(player,
                plugin.getConfig().getInt("ranch.harvest-experience", 5));
        player.sendMessage(plugin.languageService().message(player, messageKey));
        return true;
    }

    private void replaceHand(Player player, EquipmentSlot hand, ItemStack item) {
        if (hand == EquipmentSlot.OFF_HAND) {
            player.getInventory().setItemInOffHand(item);
        } else {
            player.getInventory().setItemInMainHand(item);
        }
    }
}