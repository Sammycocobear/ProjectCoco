package me.scb.Abilities.Water.Plant.SeedSummoner;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.PlantAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import me.scb.Configuration.ConfigManager;
import me.scb.Utils.AbilityUtils;
import me.scb.Utils.RainbowColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ThornBarrage extends PlantAbility{
    private ArmorStand stand;
    private final double range = ConfigManager.getConfig().getDouble("Abilities.Plant.SeedSummoner.ThornBarrage.Range");
    private final double speed = ConfigManager.getConfig().getDouble("Abilities.Plant.SeedSummoner.ThornBarrage.Speed");
    private final double damage = ConfigManager.getConfig().getDouble("Abilities.Plant.SeedSummoner.ThornBarrage.Damage");
    private final double hitbox = ConfigManager.getConfig().getDouble("Abilities.Plant.SeedSummoner.ThornBarrage.Hitbox");
    private final long cooldown = ConfigManager.getConfig().getLong("Abilities.Plant.SeedSummoner.ThornBarrage.Cooldown");
    private final long duration = ConfigManager.getConfig().getLong("Abilities.Plant.SeedSummoner.ThornBarrage.Duration");
    private final long shotDelay = ConfigManager.getConfig().getLong("Abilities.Plant.SeedSummoner.ThornBarrage.ShotDelay");
    private long nextShot;
    private int tick;
    private List<Shot> shots = new ArrayList<>();
    private static final Particle.DustOptions dus = new Particle.DustOptions(Color.BLACK,.5f);
    public ThornBarrage(Player player) {
        super(player);
        if (CoreAbility.hasAbility(player,getClass()) || !bPlayer.canBendIgnoreBinds(this)) return;
        stand = AbilityUtils.createArmorStand((player.getMainHand() == MainHand.LEFT ? getLeftHandPos() : getRightHandPos()),true, new ItemStack(Material.CACTUS));

        start();
    }

    @Override
    public void progress() {
        if (tick++ % 2 == 0){
            stand.teleport((player.getMainHand() == MainHand.LEFT ? getLeftHandPos() : getRightHandPos())
                    .add(player.getEyeLocation().getDirection().multiply(1)).subtract(0,stand.getHeight() - .2,0));
        }

        if (System.currentTimeMillis() >= nextShot){
            nextShot = System.currentTimeMillis() + shotDelay;
            Location randomLocation = stand.getLocation().add(player.getLocation().getDirection().multiply(.25)).add(0,1,0);

            double max = .2;
            randomLocation.add(ThreadLocalRandom.current().nextDouble(-max,max),
                    ThreadLocalRandom.current().nextDouble(-max,max),
                    ThreadLocalRandom.current().nextDouble(-max,max));
            shots.add(new Shot(randomLocation,GeneralMethods.getDirection(randomLocation,player.getEyeLocation().add(player.getLocation().getDirection().multiply(range))), tick++));
        }

        shots.forEach(Shot::progress);

        if (System.currentTimeMillis() - getStartTime() >= duration || !player.isSneaking()){
            remove();
            return;
        }


    }

    public void doDamage(Location location){
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location,hitbox)){
            if (AbilityUtils.isInValidEntity(entity,player)) continue;
            DamageHandler.damageEntity(entity,player,damage,this);
        }
    }

    private Location getRightHandPos() {
        return GeneralMethods.getRightSide(this.player.getLocation(), 0.64D).add(0.0D, 1D, 0.0D);
    }

    private Location getLeftHandPos() {
        return GeneralMethods.getLeftSide(this.player.getLocation(), 0.64D).add(0.0D, 1D, 0.0D);
    }

    @Override
    public void remove() {
        super.remove();
        stand.remove();
        bPlayer.addCooldown(this);
    }

    @Override
    public boolean isHiddenAbility() {
        return true;
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
        return "ThornBarrage";
    }

    @Override
    public Location getLocation() {
        return null;
    }


    public class Shot{
        private Location startLocation, origin;
        private Vector direction;
        private int ticks;
        public Shot(Location startLocation, Vector direction, int tick){
            this.startLocation = startLocation;
            this.origin = startLocation.clone();
            this.direction = direction.multiply(speed);
            this.ticks = tick;
        }

        public boolean progress(){
            if (!startLocation.getBlock().getType().isAir() || startLocation.distanceSquared(origin) >= (range * range)){
                return true;
            }
            makeLineVisual();
            doDamage(startLocation);
            startLocation.add(direction);


            return false;
        }

        public void makeLineVisual(){
            Vector direction = startLocation.getDirection().multiply(-0.5); // multiply by -0.5 to get a vector 0.5 blocks behind the starting location
            Location endLocation = startLocation.clone().add(direction); // add the direction vector to the starting location to get the end location
            if (!RainbowColor.playParticles(player,startLocation,ticks,0,0,0,.5f)){
                RainbowColor.playParticles(player,endLocation,ticks,0,0,0,.5f);
                RainbowColor.playParticles(player,startLocation.clone().add(direction.multiply(0.5)),ticks,0,0,0,.5f);
            }else{
                startLocation.getWorld().spawnParticle(Particle.REDSTONE, startLocation, 1,0,0,0,0,dus);
                startLocation.getWorld().spawnParticle(Particle.REDSTONE, endLocation, 1,0,0,0,0,dus);
                startLocation.getWorld().spawnParticle(Particle.REDSTONE, startLocation.clone().add(direction.multiply(0.5)), 1,0,0,0,0,dus);
            }
        }
    }
}
