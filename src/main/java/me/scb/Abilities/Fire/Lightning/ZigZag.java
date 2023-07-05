package me.scb.Abilities.Fire.Lightning;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.LightningAbility;
import me.scb.Utils.RainbowColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ZigZag {
    private Location location;
    private Location target;

    private Player player;
    private double speed;
    private double increment;
    private int iterations = 0;
    public ZigZag(Location location, Player player) {
        this.location = location;
        this.player = player;
        this.speed = .2;
        increment = .1;
        zigZag();
    }

    public ZigZag(Location location, Location target, Player player) {
        this.location = location;
        this.speed = .2;
        increment = .1;
        this.target = target;
        this.player = player;
        zigZag();
    }

    public ZigZag(Location location, Player player,double speed) {
        this.location = location;
        this.player = player;
        this.speed = speed;
        increment = .1;
        zigZag();
    }

    public ZigZag(Location location, Player player,double speed,double increment) {
        this.location = location;
        this.player = player;
        this.speed = speed;
        this.increment = increment;
        zigZag();
    }

    public boolean zigZag() {
        Vector direction = GeneralMethods.getDirection(location, target == null ? player.getEyeLocation() : target).multiply(speed);
        Vector ortho = GeneralMethods.getOrthogonalVector(direction, Math.random() * 360, 0.5 + Math.random() * 0.5);
        Vector out = direction.clone().add(ortho).multiply(increment);
        Vector in = direction.clone().subtract(ortho).multiply(increment);
        double distance = speed / Math.cos(direction.angle(out));
        if (location.distanceSquared(target == null ? player.getEyeLocation() : target) < 1){
            return true;
        }
        for (double d = 0; d < distance; d += increment) {
            location.add(out);
            if (RainbowColor.playParticles(player, location,iterations++)) {
                LightningAbility.playLightningbendingParticle(location, 0, 0, 0);
            }
        }

        for (double d = 0; d < distance; d += increment) {
            location.add(in);
            if (RainbowColor.playParticles(player, location,iterations++)) {
                LightningAbility.playLightningbendingParticle(location, 0, 0, 0);
            }
        }
        return false;

    }

    public Location getLocation(){
        return location;
    }

}

