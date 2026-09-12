package com.example.breedinglog.animal;

import com.example.breedinglog.BreedingLogPlugin;
import com.example.breedinglog.data.AnimalData;
import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class BreedingService {
    private static final Map<EntityType, Material> FOOD = Map.ofEntries(
            Map.entry(EntityType.COW, Material.WHEAT),
            Map.entry(EntityType.MOOSHROOM, Material.WHEAT),
            Map.entry(EntityType.SHEEP, Material.WHEAT),
            Map.entry(EntityType.GOAT, Material.WHEAT),
            Map.entry(EntityType.PIG, Material.CARROT),
            Map.entry(EntityType.CHICKEN, Material.WHEAT_SEEDS),
            Map.entry(EntityType.RABBIT, Material.DANDELION),
            Map.entry(EntityType.WOLF, Material.BEEF),
            Map.entry(EntityType.CAT, Material.COD),
            Map.entry(EntityType.OCELOT, Material.COD),
            Map.entry(EntityType.HORSE, Material.GOLDEN_CARROT),
            Map.entry(EntityType.DONKEY, Material.GOLDEN_CARROT),
            Map.entry(EntityType.LLAMA, Material.HAY_BLOCK),
            Map.entry(EntityType.PANDA, Material.BAMBOO),
            Map.entry(EntityType.FOX, Material.SWEET_BERRIES),
            Map.entry(EntityType.BEE, Material.POPPY),
            Map.entry(EntityType.TURTLE, Material.SEAGRASS),
            Map.entry(EntityType.AXOLOTL, Material.TROPICAL_FISH_BUCKET),
            Map.entry(EntityType.FROG, Material.SLIME_BALL),
            Map.entry(EntityType.CAMEL, Material.CACTUS),
            Map.entry(EntityType.ARMADILLO, Material.SPIDER_EYE));

    private final BreedingLogPlugin plugin;

    public BreedingService(BreedingLogPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isBreedingFood(Entity entity, ItemStack item) {
        return AnimalRegistry.isSupported(entity)
                && entity.getType() != EntityType.MULE
                && item != null
                && item.getType() == FOOD.get(entity.getType());
    }

    public void tryBreed(Player player, Entity animal) {
        if (!AnimalData.isAdopted(animal, plugin.pdcKeys())) {
            player.sendMessage(plugin.languageService().message(player, "need-adopt"));
            return;
        }
        UUID owner = AnimalData.owner(animal, plugin.pdcKeys());
        if (!player.getUniqueId().equals(owner)) {
            player.sendMessage(plugin.languageService().message(player, "not-member"));
            return;
        }
        if (!isAdult(animal) || isUntamed(animal)) {
            player.sendMessage(plugin.languageService().message(player, "need-adult"));
            return;
        }
        if (AnimalData.affection(animal, plugin.pdcKeys(), 50)
                < plugin.getConfig().getInt("affection.breeding-threshold", 60)) {
            player.sendMessage(plugin.languageService().message(player, "affection-low"));
            return;
        }
        if (AnimalData.isPregnant(animal, plugin.pdcKeys())) {
            player.sendMessage(plugin.languageService().message(player, "pregnant"));
            return;
        }

        Gender gender = AnimalData.gender(animal, plugin.pdcKeys());
        if (gender == null) {
            gender = Gender.random();
            animal.getPersistentDataContainer().set(plugin.pdcKeys().gender(),
                    org.bukkit.persistence.PersistentDataType.STRING, gender.name());
        }
        Entity partner = findPartner(animal, gender);
        if (partner == null) {
            player.sendMessage(plugin.languageService().message(player, "no-partner"));
            return;
        }

        long end = System.currentTimeMillis() + pregnancyMillis(animal.getType());
        Entity mother = gender == Gender.FEMALE ? animal : partner;
        AnimalData.setPregnancy(mother, plugin.pdcKeys(), end);
        consumeFood(player);
        plugin.ranchService().addExperience(player,
            plugin.getConfig().getInt("ranch.breeding-experience", 25));
        player.sendMessage(plugin.languageService().message(player, "breed-success",
            pregnancyMinutes(animal.getType())));
        Entity finalMother = mother;
        plugin.scheduler().runDelayedAtEntity(finalMother, () -> finishPregnancy(finalMother),
                pregnancyMillis(animal.getType()) / 50L);
    }

    private Entity findPartner(Entity animal, Gender gender) {
        for (Entity nearby : animal.getNearbyEntities(8, 4, 8)) {
            if (nearby.getType() != animal.getType() || !AnimalData.isAdopted(nearby, plugin.pdcKeys())
                    || !isAdult(nearby) || isUntamed(nearby) || AnimalData.isPregnant(nearby, plugin.pdcKeys())) {
                continue;
            }
            Gender partnerGender = AnimalData.gender(nearby, plugin.pdcKeys());
            if (partnerGender != null && partnerGender != gender
                    && AnimalData.affection(nearby, plugin.pdcKeys(), 50)
                    >= plugin.getConfig().getInt("affection.breeding-threshold", 60)) {
                return nearby;
            }
        }
        return null;
    }

    private void finishPregnancy(Entity mother) {
        if (!mother.isValid() || !AnimalData.hasPregnancy(mother, plugin.pdcKeys())) {
            return;
        }
        AnimalData.clearPregnancy(mother, plugin.pdcKeys());
        ItemStack egg = new ItemStack(Material.EGG);
        ItemMeta meta = egg.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "繁殖蛋：" + mother.getType().name());
        meta.getPersistentDataContainer().set(plugin.pdcKeys().breedingEgg(), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(plugin.pdcKeys().eggSpecies(), PersistentDataType.STRING,
                mother.getType().name());
        egg.setItemMeta(meta);
        mother.getWorld().dropItemNaturally(mother.getLocation(), egg);
        UUID owner = AnimalData.owner(mother, plugin.pdcKeys());
        if (owner != null) {
            Player ownerPlayer = plugin.getServer().getPlayer(owner);
            if (ownerPlayer != null) {
                plugin.ranchService().addExperience(ownerPlayer,
                        plugin.getConfig().getInt("ranch.birth-experience", 50));
                ownerPlayer.sendMessage(ChatColor.GREEN + "你的動物產下了繁殖蛋，右鍵使用即可孵化。 ");
            }
        }
        mother.getWorld().getPlayers().stream()
                .filter(player -> player.getLocation().distanceSquared(mother.getLocation()) <= 64 * 64)
                .forEach(player -> player.sendMessage(plugin.languageService().message(player, "egg-produced")));
    }

    private boolean isAdult(Entity entity) {
        return !(entity instanceof Ageable ageable) || ageable.isAdult();
    }

    private boolean isUntamed(Entity entity) {
        return entity instanceof Tameable tameable && !tameable.isTamed();
    }

    private void consumeFood(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getAmount() <= 1) {
            player.getInventory().setItemInMainHand(null);
        } else {
            item.setAmount(item.getAmount() - 1);
        }
    }

    private long pregnancyMinutes(EntityType type) {
        return plugin.getConfig().getLong("pregnancy.minutes." + type.name(),
                plugin.getConfig().getLong("pregnancy.default-minutes", 2));
    }

    private long pregnancyMillis(EntityType type) {
        return pregnancyMinutes(type) * 60_000L;
    }
}
