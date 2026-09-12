package com.example.breedinglog.command;

import com.example.breedinglog.BreedingLogPlugin;
import com.example.breedinglog.animal.AnimalRegistry;
import com.example.breedinglog.animal.Gender;
import com.example.breedinglog.data.AnimalData;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public final class BreedingCommand implements CommandExecutor {
    private final BreedingLogPlugin plugin;

    public BreedingCommand(BreedingLogPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("info")) {
            return info(sender);
        }
        if (args[0].equalsIgnoreCase("toggle")) {
            return toggle(sender);
        }
        if (args[0].equalsIgnoreCase("gender")) {
            return gender(sender);
        }
        if (args[0].equalsIgnoreCase("transfer")) {
            return transfer(sender);
        }
        if (args[0].equalsIgnoreCase("lang")) {
            return lang(sender, args);
        }
        if (args[0].equalsIgnoreCase("setgender")) {
            return setGender(sender, args);
        }
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("breedinglog.admin")) {
                sender.sendMessage(plugin.languageService().message(sender instanceof Player player ? player : null, "admin-only"));
                return true;
            }
            plugin.reloadConfig();
            plugin.dropService().reload();
            plugin.languageService().reload();
            sender.sendMessage(plugin.languageService().message(sender instanceof Player player ? player : null, "reloaded"));
            return true;
        }
        sender.sendMessage(plugin.languageService().message(sender instanceof Player player ? player : null, "usage"));
        return true;
    }

    private boolean info(CommandSender sender) {
        if (!sender.hasPermission("breedinglog.info")) {
            sender.sendMessage(plugin.languageService().message(sender instanceof Player player ? player : null, "permission"));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.languageService().message(null, "player-only"));
            return true;
        }
        int level = player.getPersistentDataContainer().getOrDefault(
                plugin.pdcKeys().ranchLevel(), PersistentDataType.INTEGER, 1);
        int experience = player.getPersistentDataContainer().getOrDefault(
                plugin.pdcKeys().ranchExperience(), PersistentDataType.INTEGER, 0);
        player.sendMessage(plugin.languageService().message(player, "ranch-info", level, experience,
            plugin.ranchService().experienceRequired(level)));
        return true;
    }

    private boolean toggle(CommandSender sender) {
        if (!sender.hasPermission("breedinglog.toggle")) {
            sender.sendMessage(plugin.languageService().message(sender instanceof Player player ? player : null, "permission"));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.languageService().message(null, "player-only"));
            return true;
        }
        byte current = player.getPersistentDataContainer().getOrDefault(
                plugin.pdcKeys().displayEnabled(), PersistentDataType.BYTE, (byte) 1);
        byte next = current == 0 ? (byte) 1 : (byte) 0;
        player.getPersistentDataContainer().set(plugin.pdcKeys().displayEnabled(), PersistentDataType.BYTE, next);
        player.sendMessage(plugin.languageService().message(player, next == 1 ? "display-on" : "display-off"));
        return true;
    }

    private boolean gender(CommandSender sender) {
        if (!sender.hasPermission("breedinglog.gender")) {
            sender.sendMessage(plugin.languageService().message(sender instanceof Player player ? player : null, "permission"));
            return true;
        }
        Player player = playerSender(sender);
        if (player == null) {
            return true;
        }
        Entity animal = targetedAnimal(player);
        if (animal == null) {
            return true;
        }
        Gender gender = AnimalData.gender(animal, plugin.pdcKeys());
        player.sendMessage(plugin.languageService().message(player, "gender", gender == null ? "?" : gender.symbol()));
        return true;
    }

    private boolean transfer(CommandSender sender) {
        if (!sender.hasPermission("breedinglog.transfer")) {
            sender.sendMessage(plugin.languageService().message(sender instanceof Player player ? player : null, "permission"));
            return true;
        }
        Player player = playerSender(sender);
        if (player == null) {
            return true;
        }
        Entity animal = targetedAnimal(player);
        if (animal == null) {
            return true;
        }
        if (!AnimalData.isAdopted(animal, plugin.pdcKeys())) {
            player.sendMessage(plugin.languageService().message(player, "not-adopted"));
            return true;
        }
        UUID owner = AnimalData.owner(animal, plugin.pdcKeys());
        if (!player.hasPermission("breedinglog.admin") && !player.getUniqueId().equals(owner)) {
            player.sendMessage(plugin.languageService().message(player, "not-owner"));
            return true;
        }
        AnimalData.transfer(animal, plugin.pdcKeys(), player.getUniqueId());
        player.sendMessage(plugin.languageService().message(player, "transferred"));
        return true;
    }

    private boolean lang(CommandSender sender, String[] args) {
        if (!sender.hasPermission("breedinglog.lang")) {
            sender.sendMessage(plugin.languageService().message(sender instanceof Player player ? player : null, "permission"));
            return true;
        }
        Player player = playerSender(sender);
        if (player == null) {
            return true;
        }
        if (args.length < 2 || !(args[1].equalsIgnoreCase("zh_TW")
                || args[1].equalsIgnoreCase("en_US") || args[1].equalsIgnoreCase("zh_CN"))) {
            player.sendMessage(plugin.languageService().message(player, "lang-usage"));
            return true;
        }
        player.getPersistentDataContainer().set(plugin.pdcKeys().language(), PersistentDataType.STRING, args[1]);
        player.sendMessage(plugin.languageService().message(player, "lang-set", args[1]));
        return true;
    }

    private boolean setGender(CommandSender sender, String[] args) {
        if (!sender.hasPermission("breedinglog.admin")) {
            sender.sendMessage(plugin.languageService().message(sender instanceof Player player ? player : null, "admin-only"));
            return true;
        }
        Player player = playerSender(sender);
        if (player == null || args.length < 2) {
            sender.sendMessage(plugin.languageService().message(player, "setgender-usage"));
            return true;
        }
        Entity animal = targetedAnimal(player);
        if (animal == null) {
            return true;
        }
        try {
            Gender gender = Gender.valueOf(args[1].toUpperCase());
            animal.getPersistentDataContainer().set(plugin.pdcKeys().gender(), PersistentDataType.STRING, gender.name());
            plugin.animalNameService().update(animal);
            player.sendMessage(plugin.languageService().message(player, "gender-set", gender.symbol()));
        } catch (IllegalArgumentException exception) {
            player.sendMessage(plugin.languageService().message(player, "gender-invalid"));
        }
        return true;
    }

    private Player playerSender(CommandSender sender) {
        if (sender instanceof Player player) {
            return player;
        }
        sender.sendMessage(plugin.languageService().message(sender instanceof Player player ? player : null, "player-only"));
        return null;
    }

    private Entity targetedAnimal(Player player) {
        Entity target = player.getTargetEntity(6);
        if (target == null || !AnimalRegistry.isSupported(target)) {
            player.sendMessage(plugin.languageService().message(player, "target-animal"));
            return null;
        }
        return target;
    }
}
