package me.scb.Abilities.Water.Blood.BloodUtils;

import me.scb.Utils.RainbowColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SourceAnimation {
    private final Player player;
    private final LivingEntity sourceEntity;
    private double progress;
    private final double speed;
    private int index = 0;

    public SourceAnimation(LivingEntity entity, Player player) {
        this.sourceEntity = entity;
        this.player = player;
        this.progress = 0;
        this.speed = .1;
    }

    public boolean createBloodStream() {
        Location sourceLocation = sourceEntity.getLocation();
        Location targetLocation = player.getLocation().add(0, 1, 0);
        Vector direction = targetLocation.toVector().subtract(sourceLocation.toVector()).normalize();
        double distance = sourceLocation.distance(targetLocation);

        // update progress based on speed and remaining distance
        double increment = speed * distance;
        progress += increment;

        // spawn particles along the line at regular intervals
        double interval = 0.1; // adjust to change density of particles
        for (double i = progress - increment; i < progress; i += interval) {
            if (i < 0 || i > distance) {
                continue; // skip if outside the start and end points
            }
            Vector offset = direction.clone().multiply(i);
            Location spawnLocation = sourceLocation.clone().add(offset);
            if (RainbowColor.playParticles(player,spawnLocation,index)) {
                spawnLocation.getWorld().spawnParticle(Particle.REDSTONE, spawnLocation, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 2));
            }
            index++;
        }

        return progress >= distance;
    }
}

