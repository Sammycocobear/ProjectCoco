package me.scb.Abilities.Earth.Lava;

import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.LavaAbility;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.scb.ProjectCoco;

public class LavaCrush extends LavaAbility implements AddonAbility {
    private long maxDuration = 5000;//config
    private long radius = 5;
    private Set<TempBlock> tempBlocks = new HashSet<>();
    public LavaCrush(Player player) {
        super(player);
    }

    @Override
    public void progress() {


    }
    @Override
    public boolean isEnabled() {
        return false;
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
        return "LavaCrush";
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
