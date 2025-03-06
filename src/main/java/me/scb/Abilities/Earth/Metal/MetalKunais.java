package me.scb.Abilities.Earth.Metal;

import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.MetalAbility;
import me.scb.ProjectCoco;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class MetalKunais extends MetalAbility implements AddonAbility {

    public MetalKunais(Player player) {
        super(player);
    }
    @Override
    public boolean isEnabled() {
        return false;
    }
    @Override
    public void progress() {

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
        return "MetalKunais";
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
