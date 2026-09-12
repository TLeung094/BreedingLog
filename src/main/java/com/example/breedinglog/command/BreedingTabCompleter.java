package com.example.breedinglog.command;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public final class BreedingTabCompleter implements TabCompleter {
    private static final List<String> COMMANDS = List.of(
            "info", "toggle", "gender", "transfer", "lang", "setgender", "reload");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return complete(COMMANDS, args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("lang")) {
            return complete(List.of("zh_TW", "en_US", "zh_CN"), args[1]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("setgender")) {
            return complete(List.of("MALE", "FEMALE"), args[1]);
        }
        return List.of();
    }

    private List<String> complete(List<String> values, String prefix) {
        String normalized = prefix.toLowerCase(Locale.ROOT);
        return values.stream()
                .filter(value -> value.toLowerCase(Locale.ROOT).startsWith(normalized))
                .collect(Collectors.toList());
    }
}