package com.example.breedinglog.i18n;

import com.example.breedinglog.BreedingLogPlugin;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public final class LanguageService {
    private final BreedingLogPlugin plugin;
    private final Map<String, FileConfiguration> languages = new HashMap<>();

    public LanguageService(BreedingLogPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        languages.clear();
        load("zh_TW");
        load("en_US");
        load("zh_CN");
    }

    public String message(Player player, String key, Object... arguments) {
        String language = plugin.getConfig().getString("default-language", "en_US");
        if (player != null) {
            language = player.getPersistentDataContainer().getOrDefault(
                    plugin.pdcKeys().language(), org.bukkit.persistence.PersistentDataType.STRING, language);
        }
        FileConfiguration file = languages.getOrDefault(language, languages.get("en_US"));
        String value = file.getString(key, key);
        for (int index = 0; index < arguments.length; index++) {
            value = value.replace("{" + index + "}", String.valueOf(arguments[index]));
        }
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    private void load(String language) {
        File file = new File(plugin.getDataFolder(), "lang/" + language + ".yml");
        if (!file.exists()) {
            plugin.saveResource("lang/" + language + ".yml", false);
        }
        languages.put(language, YamlConfiguration.loadConfiguration(file));
    }
}