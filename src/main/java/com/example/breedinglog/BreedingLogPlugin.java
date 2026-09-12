package com.example.breedinglog;

import com.example.breedinglog.data.PdcKeys;
import com.example.breedinglog.animal.RanchService;
import com.example.breedinglog.animal.DropService;
import com.example.breedinglog.animal.AffectionDecayService;
import com.example.breedinglog.animal.PassiveDropService;
import com.example.breedinglog.animal.AnimalNameService;
import com.example.breedinglog.command.BreedingCommand;
import com.example.breedinglog.command.BreedingTabCompleter;
import com.example.breedinglog.listener.AnimalInteractionListener;
import com.example.breedinglog.listener.AnimalProgressListener;
import com.example.breedinglog.listener.BreedingEggListener;
import com.example.breedinglog.listener.PlayerDataListener;
import com.example.breedinglog.listener.AnimalStatusGuiListener;
import com.example.breedinglog.i18n.LanguageService;
import com.example.breedinglog.scheduler.FoliaSchedulerAdapter;
import com.example.breedinglog.scheduler.PaperSchedulerAdapter;
import com.example.breedinglog.scheduler.SchedulerAdapter;
import org.bukkit.plugin.java.JavaPlugin;

public final class BreedingLogPlugin extends JavaPlugin {
    private SchedulerAdapter scheduler;
    private PdcKeys pdcKeys;
    private RanchService ranchService;
    private DropService dropService;
    private LanguageService languageService;
    private AnimalNameService animalNameService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        pdcKeys = new PdcKeys(this);
        ranchService = new RanchService(this);
        dropService = new DropService(this);
        languageService = new LanguageService(this);
        animalNameService = new AnimalNameService(this);
        scheduler = isFolia() ? new FoliaSchedulerAdapter(this) : new PaperSchedulerAdapter(this);
        getServer().getPluginManager().registerEvents(new PlayerDataListener(this), this);
        getServer().getPluginManager().registerEvents(new AnimalStatusGuiListener(), this);
        getServer().getPluginManager().registerEvents(new AnimalInteractionListener(this), this);
        getServer().getPluginManager().registerEvents(new AnimalProgressListener(this), this);
        getServer().getPluginManager().registerEvents(new BreedingEggListener(this), this);
        getCommand("bbreeding").setExecutor(new BreedingCommand(this));
        getCommand("bbreeding").setTabCompleter(new BreedingTabCompleter());
        AffectionDecayService decayService = new AffectionDecayService(this);
        scheduler.runRepeatingGlobal(decayService::decayLoadedAnimals, 24_000L, 24_000L);
        PassiveDropService passiveDropService = new PassiveDropService(this);
        scheduler.runRepeatingGlobal(passiveDropService::dropFromLoadedAnimals,
            dropService.passiveIntervalTicks(), dropService.passiveIntervalTicks());
        getLogger().info("BreedingLog enabled with " + scheduler.getClass().getSimpleName());
    }

    public SchedulerAdapter scheduler() {
        return scheduler;
    }

    public PdcKeys pdcKeys() {
        return pdcKeys;
    }

    public RanchService ranchService() {
        return ranchService;
    }

    public DropService dropService() {
        return dropService;
    }

    public LanguageService languageService() {
        return languageService;
    }

    public AnimalNameService animalNameService() {
        return animalNameService;
    }

    private boolean isFolia() {
        return getServer().getName().equalsIgnoreCase("Folia");
    }
}
