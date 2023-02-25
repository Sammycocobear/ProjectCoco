package me.scb.Abilities.Earth.Lava;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.LavaAbility;
import com.projectkorra.projectkorra.util.TempBlock;
import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import me.scb.Utils.LavaUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class LavaBall extends LavaAbility implements AddonAbility {
    private double radius;
    private final double radiusIncrease = .1;
    private final double maxRadius = 5;
    public LavaBall(Player player) {
        super(player);
        location = GeneralMethods.getTargetedLocation(player,10);
        start();
    }

    public void makeSphere(Location location){
        double particles = (10 * radius) + 1;
        for (int i = 0; i < particles; i++) {
            Vector vector = AbilityUtils.getRandomVector().multiply(radius);
            location.add(vector);
            t.add(LavaUtils.createLava(location.getBlock()));
            location.subtract(vector);
        }

    }
    List<TempBlock> t = new ArrayList<>();
    public Location getLocation(Location start){
        for (double i = 0; i < 10; /* range */ i+=.5){
            final Block b = start.add(start.getDirection().multiply(i)).getBlock();
            if (!b.isPassable() && !b.hasMetadata("ProjectCoco://LavaAbility://Lava")) {
                return start;
            }else{
                start.subtract(start.getDirection().multiply(i));
            }

        }
        return start.add(start.getDirection().multiply(10));
    }
    boolean shot = false;
    Location location = null;
    @Override
    public void progress() {
        if (System.currentTimeMillis() - getStartTime() >= 7000){
            remove();
            return;
        }
        if (!shot){
            location = getLocation(player.getEyeLocation());
        }
        location.getWorld().spawnParticle(Particle.FIREWORKS_SPARK,location,1,0,0,0,0);
        if (!shot && radius < maxRadius) {
            for (Block bl : GeneralMethods.getBlocksAroundPoint(location, 2)) {
                if (bl.getType() == Material.LAVA && !bl.hasMetadata("ProjectCoco://LavaAbility://Lava")) {
                    radius += radiusIncrease;
                    break;
                }
            }
        }


        if (shot){
            location.add(player.getEyeLocation().getDirection());
        }
        if (!player.isSneaking()){
            shot = true;
        }
        if (TempBlock.isTempBlock(location.getBlock())) return;
        makeSphere(location);
    }


    @Override
    public void remove() {
        super.remove();
        t.forEach(TempBlock::revertBlock);
    }

    @Override
    public boolean isSneakAbility() {
        return false;
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public long getCooldown() {
        return 0;
    }

    @Override
    public String getName() {
        return "LavaBall";

    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getAuthor() {
        return ProjectCoco.getAuthor();
    }

    @Override
    public String getVersion() {
        return ProjectCoco.getVersion();
    }
}
