package com.example.breedinglog.data;

import com.example.breedinglog.animal.Gender;
import java.util.UUID;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class AnimalData {
    private AnimalData() {
    }

    public static boolean isAdopted(Entity entity, PdcKeys keys) {
        return entity.getPersistentDataContainer().has(keys.adopted(), PersistentDataType.BYTE);
    }

    public static UUID owner(Entity entity, PdcKeys keys) {
        String value = entity.getPersistentDataContainer().get(keys.ownerUuid(), PersistentDataType.STRING);
        return value == null ? null : UUID.fromString(value);
    }

    public static Gender gender(Entity entity, PdcKeys keys) {
        String value = entity.getPersistentDataContainer().get(keys.gender(), PersistentDataType.STRING);
        return value == null ? null : Gender.valueOf(value);
    }

    public static int affection(Entity entity, PdcKeys keys, int defaultValue) {
        Integer value = entity.getPersistentDataContainer().get(keys.affection(), PersistentDataType.INTEGER);
        return value == null ? defaultValue : value;
    }

    public static void adopt(Entity entity, PdcKeys keys, UUID owner, Gender gender, int affection) {
        PersistentDataContainer data = entity.getPersistentDataContainer();
        data.set(keys.ownerUuid(), PersistentDataType.STRING, owner.toString());
        data.set(keys.adopted(), PersistentDataType.BYTE, (byte) 1);
        data.set(keys.gender(), PersistentDataType.STRING, gender.name());
        data.set(keys.affection(), PersistentDataType.INTEGER, affection);
    }

    public static void transfer(Entity entity, PdcKeys keys, UUID owner) {
        entity.getPersistentDataContainer().set(keys.ownerUuid(), PersistentDataType.STRING, owner.toString());
    }

    public static boolean isPregnant(Entity entity, PdcKeys keys) {
        Long end = entity.getPersistentDataContainer().get(keys.pregnancyEnd(), PersistentDataType.LONG);
        return end != null && end > System.currentTimeMillis();
    }

    public static boolean hasPregnancy(Entity entity, PdcKeys keys) {
        return entity.getPersistentDataContainer().has(keys.pregnancyEnd(), PersistentDataType.LONG);
    }

    public static void setPregnancy(Entity entity, PdcKeys keys, long endMillis) {
        entity.getPersistentDataContainer().set(keys.pregnancyEnd(), PersistentDataType.LONG, endMillis);
    }

    public static void clearPregnancy(Entity entity, PdcKeys keys) {
        entity.getPersistentDataContainer().remove(keys.pregnancyEnd());
    }

    public static int changeAffection(Entity entity, PdcKeys keys, int amount, int minimum, int maximum) {
        int next = Math.clamp(affection(entity, keys, 50) + amount, minimum, maximum);
        entity.getPersistentDataContainer().set(keys.affection(), PersistentDataType.INTEGER, next);
        return next;
    }
}
