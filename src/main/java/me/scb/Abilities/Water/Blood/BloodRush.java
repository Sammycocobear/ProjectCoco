package me.scb.Abilities.Water.Blood;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.BloodAbility;
import me.scb.Configuration.ConfigManager;
import me.scb.Utils.AbilityUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BloodRush extends BloodAbility implements AddonAbility {
    private SourceAnimation s;
    private final double range = ConfigManager.getConfig().getDouble("Abilities.Blood.BloodRush.Range");

    private static final PotionEffect p = new PotionEffect(PotionEffectType.SPEED,1,3);
    boolean canStart = false;
    private long startTime = -1;
    public BloodRush(Player player) {
        super(player);
        final Entity target = GeneralMethods.getTargetedEntity(player,range);

        if (!(target instanceof LivingEntity)) return;
        s = new SourceAnimation((LivingEntity) target,player);


        start();
    }
    List<Location> poolLocation = new ArrayList<>();
    public void makeAnimation() {
        // Get the locations for the left and right sides of the line
        Location behindLocation = player.getLocation();
        behindLocation.setPitch(0);
        behindLocation.subtract(behindLocation.getDirection().multiply(2));
        Location left = GeneralMethods.getLeftSide(behindLocation, 2);
        Location right = GeneralMethods.getRightSide(behindLocation, 2);

        // Determine the direction of the line
        Vector direction = right.toVector().subtract(left.toVector()).normalize();

        // Determine the number of particles to spawn along the line
        int particleCount = 10; // adjust as needed

        // Calculate the interval between particles
        double distance = left.distance(right);
        double interval = distance / (particleCount - 1);

        // Spawn particles along the line
        for (int i = 0; i < particleCount; i++) {
            Vector offset = direction.clone().multiply(interval * i);
            Location spawnLocation = left.clone().add(offset);
            if (!poolLocation.contains(spawnLocation)){
                poolLocation.add(spawnLocation);
            }
            spawnLocation.getWorld().spawnParticle(Particle.REDSTONE, spawnLocation, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 2));
        }
    }

    public void applySpeedBoost(Player player, double boostFactor) {
        // Calculate the velocity boost to apply to the player
        Vector boost = player.getLocation().getDirection().normalize().multiply(boostFactor);
        player.setVelocity(boost);

    }

    @Override
    public void progress() {
        if (!canStart && s.createBloodStream()){
            canStart = true;
            startTime = System.currentTimeMillis();
        }

        if (!canStart) return;
        if (System.currentTimeMillis() - startTime >= 5000){
            remove();
            return;
        }
        //The move
        player.addPotionEffect(p);
        makeAnimation();
        poolLocation.forEach(l -> l.getWorld().spawnParticle(Particle.REDSTONE, l, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 2)));

    }

    @Override
    public void remove() {
        super.remove();
        player.setVelocity(new Vector(0, player.getVelocity().getY(), 0)); // reset the player's velocity in the y direction
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
        return "BloodRush";
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
