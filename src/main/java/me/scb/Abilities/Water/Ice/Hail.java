package me.scb.Abilities.Water.Ice;

import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.IceAbility;
import me.scb.Abilities.Water.RainCloud;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class Hail extends IceAbility implements AddonAbility {
    public Hail(Player player) {
        super(player);
        List<RainCloud> rainClouds = RainCloud.getRainCloudsInArea(player,10);
        for (RainCloud rainCloud : rainClouds){
            if (rainCloud.getPlayer() != player){
                rainCloud.setPlayer(player);
            }
            rainCloud.setHailing(true,this);
        }
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
        return "Hail";
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
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }
}
