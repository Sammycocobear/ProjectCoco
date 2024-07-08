package me.scb.Abilities.Fire.Lightning;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.LightningAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Railgun extends LightningAbility implements AddonAbility {
    private final long minChargeTime = ConfigManager.getConfig().getLong("Abilities.Lightning.Railgun.MinimumChargeTime");
    private final long maxChargeTime = ConfigManager.getConfig().getLong("Abilities.Lightning.Railgun.MaxChargeTime");
    private final double hitbox = ConfigManager.getConfig().getLong("Abilities.Lightning.Railgun.Hitbox");
    private final double damage = ConfigManager.getConfig().getLong("Abilities.Lightning.Railgun.Damage");
    private final double rangeMultiplier = ConfigManager.getConfig().getLong("Abilities.Lightning.Railgun.RangeMultiplier");
    private final long cooldown = ConfigManager.getConfig().getLong("Abilities.Lightning.Railgun.Cooldown");


    private boolean hasShot;
    private int count = 0;
    private List<Entity> entityList = new ArrayList<>();
    private List<ZigZag> zigZags = new ArrayList<>();

    public Railgun(Player player) {
        super(player);
        if (CoreAbility.hasAbility(player,getClass()) || bPlayer.isOnCooldown(this) || !bPlayer.canBend(this)){
            return;
        }
        start();
    }

    public boolean canShoot(){
        return System.currentTimeMillis() - getStartTime() >= minChargeTime;
    }


    double chargePercentage = 0;
    public void updateChargeDisplay(Player player) {
        long elapsedTime = System.currentTimeMillis() - getStartTime();
        if (elapsedTime <= maxChargeTime){
            chargePercentage = (double) elapsedTime / (double) maxChargeTime;
        } else {
            chargePercentage = 1.0;
        }

        String chargeMessage = canShoot() ? String.format(ChatColor.BLUE + "Ready to release... %d%%", (int) (chargePercentage * 100))
                : String.format(ChatColor.RED + "Charging... %d%%", (int) (chargePercentage * 100));

        player.sendActionBar(chargeMessage);
    }




    private Location getRandomLocation(){
        return player.getLocation().add(new Vector(ThreadLocalRandom.current().nextDouble(-1,1) * 5,ThreadLocalRandom.current().nextDouble() * 5,ThreadLocalRandom.current().nextDouble(-1,1) * 5));
    }

    private void zigZag(Location location) {
        zigZags.add(new ZigZag(location,player, 0.5));
    }


    private void zigZag() {
        zigZags.add(new ZigZag(getRandomLocation(),player));
    }

    public Location getGround(Location location) {
        final Block standingblock = location.getBlock();
        for (int i = 0; i <= 60; i++) {
            final Block block = standingblock.getRelative(BlockFace.DOWN, i);
            if (GeneralMethods.isSolid(block)) {
                return location.clone().subtract(0,location.getY() - block.getLocation().getY(),0).add(0,1.1,0);
            }
        }
        return null;
    }


    private void createCircle(Location location){
        int particleCount = 12;
        int rand = ThreadLocalRandom.current().nextInt(0,particleCount);
        for (int i = 0; i < particleCount; i++) {
            double x = (1 * Math.sin(Math.toRadians(360.0 / particleCount) * i));
            double z = (1 * Math.cos(Math.toRadians(360.0 / particleCount) * i));
            Location ground = getGround(location);
            ground.add(x, 0, z);
            if (rand == i){
                player.spawnParticle(Particle.END_ROD,ground,1,.2,.2,.2,.1);
            }
            player.spawnParticle(Particle.REDSTONE,ground,1,0,0,0,0,new Particle.DustOptions(Color.fromRGB(1,225,255),1.5f));
            //ground.subtract(x, 0, z);

        }
    }



    public void doDamage(Location location) {
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, hitbox)) {
            if (AbilityUtils.isInValidEntity(entity, player) || entityList.contains(entity)) continue;
            entityList.add(entity);
            DamageHandler.damageEntity(entity,player,damage,this);
        }

    }

    public void teleport(){
        hasShot = true;
        zigZags.clear();
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, player.getLocation(), 1);
        player.getWorld().spawnParticle(Particle.FLASH, player.getLocation(), 1);
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 3, 0.5, 0.5, 0.5);
        for (int i = 0; i < 5; i++) {
            zigZag(player.getLocation().add(player.getLocation().getDirection().multiply(-3)).add(Math.random() * 5, Math.random() * 10, Math.random() * 5));
        }
        Location location = player.getLocation();
        location.setPitch(0);
        player.teleport(getGround(getTargetedLocation(location,chargePercentage * rangeMultiplier)));
        player.getWorld().spawnParticle(Particle.SMOKE_LARGE, player.getLocation(), 1);
        player.getWorld().spawnParticle(Particle.FLASH, player.getLocation(), 2);
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 3, 0.5, 0.5, 0.5);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, .5f, 1.0f);
    }


    public Location getTargetedLocation(Location startLocation, double range) {
        Location targetLocation = null;

        // Calculate the end location of the target ray
        Vector direction = startLocation.getDirection();
        Location endLocation = startLocation.clone().add(direction.clone().multiply(range));

        // Get the last solid block hit by the target ray
        RayTraceResult result = startLocation.getWorld().rayTraceBlocks(startLocation, direction, range);
        if (result != null && result.getHitBlock() != null && result.getHitBlock().getType().isSolid()) {
            // Get the location of the block one block before the hit block
            targetLocation = result.getHitBlock().getLocation();
        }

        // If the target block was not found, fall back to the end location of the target ray
        if (targetLocation == null) {
            targetLocation = endLocation.getBlock().getLocation();
        }

        targetLocation.subtract(direction.multiply(1.5));
        // Set the yaw and pitch of the target location to the same as the start location
        targetLocation.setYaw(startLocation.getYaw());
        targetLocation.setPitch(player.getLocation().getPitch());

        return targetLocation.add(0.5, 0.5, 0.5);
    }





    @Override
    public void progress() {
        if (player.isDead() || !player.isOnline()) {
            remove();
            return;
        }else if (GeneralMethods.isRegionProtectedFromBuild(player,player.getLocation())){
            remove();
            return;
        }
        if (player.isSneaking() && !hasShot) {
            updateChargeDisplay(player);
            if (count++ == 6) {
                for (int i = 0; i < (int) (Math.random() * 3) + 1; i++) {
                    zigZag();
                }
                count = 0;
            }
            if (canShoot()){
                Location location = player.getLocation();
                location.setPitch(0);
                createCircle(getTargetedLocation(location,chargePercentage * rangeMultiplier));
            }
        } else if (canShoot() && !hasShot) {
            teleport();
        } else if (zigZags.isEmpty()) {
            remove();
            return;
        }

        if (hasShot) {
            if (count++==6) {
                zigZag(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(-3)).add(Math.random() * 5, Math.random() * 10, Math.random() * 5));
                count = 0;
            }
            doDamage(zigZags.get(0).getLocation());

        }
        zigZags.removeIf(ZigZag::zigZag);


    }

    @Override
    public void remove() {
        super.remove();
        bPlayer.addCooldown(this,cooldown);
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
        return cooldown;
    }

    @Override
    public String getName() {
        return "Railgun";
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
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

    public String getInstructions(){
        return "Hold sneak and then release to teleport.";
    }

    public String getDescription(){
        return "While sneaking, charge your Railgun, and upon releasing the sneak button, you will teleport to a location in front of you. The distance of the teleportation will depend on the duration of the charge. The teleportation will damage any entities located between your starting position and the teleported location.";
    }
}
