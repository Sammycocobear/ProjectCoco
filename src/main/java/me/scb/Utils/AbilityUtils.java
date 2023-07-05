package me.scb.Utils;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import me.scb.ProjectCoco;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
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


    public static FallingBlock createFallingBlock(Location loc, Material type) {
        FallingBlock fallingBlock = loc.getWorld().spawnFallingBlock(loc, type.createBlockData());
        fallingBlock.setDropItem(false);
        fallingBlock.setMetadata("ProjectCoco://TempFallingBlock",getValue());
        return fallingBlock;
    }
    public static ArmorStand createArmorStand(Location loc, boolean small, ItemStack head){
        return loc.getWorld().spawn(loc, ArmorStand.class, stand -> {
            stand.setVisible(false);
            stand.setSmall(small);
            EntityEquipment equipment = stand.getEquipment();
            equipment.setHelmet(new ItemStack(head));
            stand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
        });
    }

    public static ArmorStand createArmorStand(Location loc, ItemStack head){
        return loc.getWorld().spawn(loc, ArmorStand.class, stand -> {
            stand.setVisible(false);
            stand.setHelmet(head);
        });
    }

    public static ArmorStand createArmorStand(Location loc,boolean small){
        return loc.getWorld().spawn(loc, ArmorStand.class, stand -> {
            stand.setVisible(false);
            stand.setSmall(small);
        });
    }

    public static ArmorStand createArmorStand(Location loc){
        return loc.getWorld().spawn(loc, ArmorStand.class, stand -> stand.setVisible(false));
    }

    public static MetadataValue getValue(){
        return value;
    }

    //From EffectLib
    public static Vector getRandomVector() {
        double x, y, z;
        x = random.nextDouble() * 2 - 1;
        y = random.nextDouble() * 2 - 1;
        z = random.nextDouble() * 2 - 1;

        return new Vector(x, y, z).normalize();
    }

    public static Vector getRandomCircleVector() {
        double rnd, x, z;
        rnd = random.nextDouble() * 2 * Math.PI;
        x = Math.cos(rnd);
        z = Math.sin(rnd);

        return new Vector(x, 0, z);
    }

    public static Item dropItem(Location spawnLocation, Material type) {
        Item returnItem = spawnLocation.getWorld().dropItem(spawnLocation, new ItemStack(type));
        returnItem.setMetadata("ProjectCoco://Dropped_Item", value);
        returnItem.setCanPlayerPickup(false);
        returnItem.setCanMobPickup(false);
        return returnItem;
    }

}
