package me.scb.Abilities.Air.Sound;

import com.projectkorra.projectkorra.ability.AddonAbility;
import me.scb.Abilities.Air.Sound.SoundElement.SoundAbility;
import me.scb.ProjectCoco;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BassBoost extends SoundAbility implements AddonAbility {
    public BassBoost(Player player) {
        super(player);
        start();
    }
    Location location = player.getLocation();
    int step = 0;
    @Override
    public void progress() {

        if (System.currentTimeMillis() - getStartTime() >= 5000){
            remove();
            return;
        }

        for (int x = 0; x < 2; x++) {
            for (int i = 0; i < 2; i++) {
                double angle = step * player.getWidth() + (2 * Math.PI * i / 2);
                Vector v = new Vector(Math.cos(angle) * player.getWidth(), step * .0625, Math.sin(angle) * player.getWidth());
                location.add(v);
                location.getWorld().spawnParticle(Particle.END_ROD,location,1,0,0,0,0);
                location.subtract(v);
            }
            step++;
        }
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
        return "BassBoost";
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
