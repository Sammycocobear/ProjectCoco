package me.scb.Utils;

import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class AbilityUtils {
    public static final Random random = ThreadLocalRandom.current();

    public static String getConfigPatch(CoreAbility s,String thing){
        return "Abilities." + s.getElement().getName() + "." + s.getName() + "." + thing;
    }

    public static boolean isInValidEntity(Entity e, Player player){
        return !(e instanceof LivingEntity) || e instanceof ArmorStand || e.equals(player);
    }

    public static boolean isInValidEntity(Entity e){
        return !(e instanceof LivingEntity) || e instanceof ArmorStand;
    }

    public static Vector getRandomVector() {
        double x, y, z;
        x = random.nextDouble() * 2 - 1;
        y = random.nextDouble() * 2 - 1;
        z = random.nextDouble() * 2 - 1;

        return new Vector(x, y, z).normalize();
    }
}
