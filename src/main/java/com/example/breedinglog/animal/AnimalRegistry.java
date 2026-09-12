package com.example.breedinglog.animal;

import java.util.EnumSet;
import java.util.Set;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public final class AnimalRegistry {
    private static final Set<EntityType> SUPPORTED = EnumSet.of(
            EntityType.COW, EntityType.MOOSHROOM, EntityType.SHEEP, EntityType.GOAT,
            EntityType.PIG, EntityType.CHICKEN, EntityType.RABBIT, EntityType.WOLF,
            EntityType.CAT, EntityType.OCELOT, EntityType.HORSE, EntityType.DONKEY,
            EntityType.LLAMA, EntityType.PANDA, EntityType.FOX, EntityType.BEE,
            EntityType.TURTLE, EntityType.AXOLOTL, EntityType.FROG, EntityType.CAMEL,
            EntityType.ARMADILLO, EntityType.MULE);

    private AnimalRegistry() {
    }

    public static boolean isSupported(Entity entity) {
        return SUPPORTED.contains(entity.getType());
    }
}
