package me.scb.Abilities.Earth.Sand;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.SandAbility;
import com.projectkorra.projectkorra.util.TempBlock;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class QuickSand extends SandAbility implements AddonAbility {
    private final int maxRadius = ConfigManager.getConfig().getInt("Abilities.Sand.QuickSand.Radius");
    private final long radiusIncreaseDelay = ConfigManager.getConfig().getLong("Abilities.Sand.QuickSand.RadiusIncreaseDelay");
    private final long duration = ConfigManager.getConfig().getLong("Abilities.Sand.QuickSand.Duration");
    private long next;
    private int radius = 0;
    private final List<TempBlock> tempBlocks = new ArrayList<>();
    private final List<FallingBlock> fallingBlocks = new ArrayList<>();
    private Location origin;
    List<Location> circle;
    public QuickSand(Player player) {
        super(player);
        int range = ConfigManager.getConfig().getInt("Abilities.Sand.QuickSand.SourceRange");
        final Block loc = player.getTargetBlockExact(range);
        if (loc == null) return;
        origin = loc.getLocation().add(.5,.5,.5);
        circle = GeneralMethods.getCircle(origin, ++radius, 1, false, true, 0);



        start();
    }

    public void makeCircle() {

        if (System.currentTimeMillis() >= next && radius < maxRadius) {
            next = System.currentTimeMillis() + radiusIncreaseDelay;
            circle = GeneralMethods.getCircle(origin, ++radius, 1, false, false, 0);
        }


        for (Location l : circle) {
            l.clone().add(.5, .5, .5);
            final Block b = l.getBlock();
            if (!isEarthbendable(b)) continue;
            tempBlocks.add(new TempBlock(b, Material.AIR));
            FallingBlock fb = AbilityUtils.createFallingBlock(l.add(.5,.5,.5),Material.SAND);
            fb.setVelocity(new Vector(0,Math.random() * .15,0));
            fallingBlocks.add(fb);
        }

        for (Location l : GeneralMethods.getCircle(origin, radius + 1, 1, true, false, 0)) {
            final Block b = l.getBlock();
            if (!isEarthbendable(b)) continue;
            tempBlocks.add(new TempBlock(b, Material.SAND));
        }
    }



    public void moveFallingBlocks(){
        ListIterator<FallingBlock> iter = fallingBlocks.listIterator();
        while(iter.hasNext()){
            final FallingBlock fb = iter.next();
            if(fb.isDead()){
                FallingBlock f = AbilityUtils.createFallingBlock(fb.getLocation(),Material.SAND);
                iter.remove();
                f.setVelocity(new Vector(0,Math.random() * .15,0));
                iter.add(f);
            }else{
                if (fb.getLocation().getY() - origin.getY() <= 0) {
                    fb.setVelocity(new Vector(0, Math.random() * .15, 0));
                }
            }
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(fb.getLocation(),.5)){
                if (AbilityUtils.isInValidEntity(entity) || !entity.getLocation().subtract(0,.1,0).getBlock().getType().isSolid()) continue;
                entity.setVelocity(entity.getVelocity().multiply(.5));
            }
            fb.getWorld().spawnParticle(Particle.BLOCK_DUST,fb.getLocation(),1,.5,.5,.5,0,Material.END_STONE.createBlockData());
        }
    }



    @Override
    public void progress() {
        if (System.currentTimeMillis() - getStartTime() >= duration){
            remove();
            return;
        }
        makeCircle();
        moveFallingBlocks();


    }

    @Override
    public void remove() {
        super.remove();
        bPlayer.addCooldown(this);
        fallingBlocks.forEach(fb -> {
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(fb.getLocation(),1)){
                if (AbilityUtils.isInValidEntity(entity) || !entity.getLocation().subtract(0,.1,0).getBlock().isSolid()) continue;
                entity.setVelocity(new Vector(0,.6,0));
            }
            fb.remove();
        });
        tempBlocks.forEach(TempBlock::revertBlock);

    }

    public String getInstructions(){
        return "Click to spawn QuickSand to wherever you're looking";
    }

    public String getDescription(){
        return "To use QuickSand, simply left-click to spawn quicksand at the targeted location. Anyone caught inside the quicksand will experience slowed movement. The quicksand will gradually expand its radius over time.";
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
        return ConfigManager.getConfig().getLong("Abilities.Sand.QuickSand.Cooldown");
    }

    @Override
    public String getName() {
        return "QuickSand";
    }

    @Override
    public Location getLocation() {
        return origin;
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
