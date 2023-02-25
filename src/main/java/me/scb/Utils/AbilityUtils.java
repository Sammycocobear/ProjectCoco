package me.scb.Utils;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import me.scb.ProjectCoco;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class AbilityUtils {
    public static final Random random = ThreadLocalRandom.current();
    private final static MetadataValue value = new FixedMetadataValue(ProjectCoco.getPlugin(),1);


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

    public static FallingBlock createFallingBlock(Location loc, Material type) {
        FallingBlock fallingBlock = loc.getWorld().spawnFallingBlock(loc, type.createBlockData());
        fallingBlock.setDropItem(false);
        fallingBlock.setMetadata("ProjectCoco://TempFallingBlock",getValue());
        return fallingBlock;
    }

    public static MetadataValue getValue(){
        return value;
    }
}
