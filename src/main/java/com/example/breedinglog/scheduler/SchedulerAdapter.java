package com.example.breedinglog.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface SchedulerAdapter {
    void runAtEntity(Entity entity, Runnable task);

    void runAtLocation(Location location, Runnable task);

    void runDelayedAtEntity(Entity entity, Runnable task, long delayTicks);

    void runGlobal(Runnable task);

    void runRepeatingGlobal(Runnable task, long initialDelayTicks, long periodTicks);
}
