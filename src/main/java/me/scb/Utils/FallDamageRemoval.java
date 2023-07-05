package me.scb.Utils;

import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class FallDamageRemoval {
    private static Map<LivingEntity, Double> tracker = new HashMap<>();

    public static boolean hasFallDamageCap(LivingEntity entity) {
        return tracker.containsKey(entity);
    }

    public static double getFallDamageCap(LivingEntity entity) {
        return tracker.get(entity);
    }

    public static void addFallDamageCap(LivingEntity entity, double damagecap){
        if (hasFallDamageCap(entity)) {
            tracker.remove(entity);
        }
        tracker.put(entity, damagecap);
    }

    public static void removeFallDamageCap(LivingEntity entity) {
        tracker.remove(entity);
    }
}
