package me.scb.Abilities.Earth.Lava;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.LavaAbility;
import com.projectkorra.projectkorra.util.TempBlock;
import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class Volcano  extends LavaAbility implements AddonAbility {
    private int radius = 5;
    private final Location source;
    private long next,delay;
    private double height = 2;
    public Volcano(Player player) {
        super(player);
        source = GeneralMethods.getTargetedLocation(player,20);
        int c = 0;
        while (source.subtract(0,1,0).getBlock().getType().isAir() && c++ < 20);
        source.add(0,1,0);
        delay = 250;
        start();
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void progress() {
        if (System.currentTimeMillis() - getStartTime() >= 5000){
            remove();
            return;
        }

        if (System.currentTimeMillis() >= next && radius > 1){
            next = System.currentTimeMillis() + delay;
            for (Location b : GeneralMethods.getCircle(source,radius,(int) height,true,false,0)){
                new TempBlock(b.getBlock(), Material.CRIMSON_HYPHAE.createBlockData(),5000);
            }
            radius--;
            height+=.5;
            source.add(0,height > 2.5 ? height - 1 : height,0);
        }
        if (radius <= 1){
            FallingBlock f = AbilityUtils.createFallingBlock(source.clone().add(0,.5,0),Material.MAGMA_BLOCK);
            f.setVelocity(new Vector(ThreadLocalRandom.current().nextDouble(-1,1), ThreadLocalRandom.current().nextDouble(),ThreadLocalRandom.current().nextDouble(-1,1)));
        }


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
        return "Volcano";
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

    public String getVersion() {
        return ProjectCoco.getVersion();
    }

}
