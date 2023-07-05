package me.scb.Abilities.Water.Plant;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.PlantAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import org.bukkit.*;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ThornyBush extends PlantAbility implements AddonAbility {
    private double sourceRange = ConfigManager.getConfig().getDouble("Abilities.Plant.ThornyBush.SourceRange");
    private int thorns = Math.min(ConfigManager.getConfig().getInt("Abilities.Plant.ThornyBush.Thorn.Thorns"),360);
    public double amplitude = ConfigManager.getConfig().getDouble("Abilities.Plant.ThornyBush.Thorn.Amplitude");
    public double wavelength = ConfigManager.getConfig().getDouble("Abilities.Plant.ThornyBush.Thorn.Wavelength");
    public double period = ConfigManager.getConfig().getDouble("Abilities.Plant.ThornyBush.Thorn.Period");
    public int particlesPerCycle = ConfigManager.getConfig().getInt("Abilities.Plant.ThornyBush.Thorn.ParticlesPerCycle");
    private double range = ConfigManager.getConfig().getDouble("Abilities.Plant.ThornyBush.Range");
    private double damage = ConfigManager.getConfig().getDouble("Abilities.Plant.ThornyBush.Thorn.Damage");
    private double hitbox = ConfigManager.getConfig().getDouble("Abilities.Plant.ThornyBush.Hitbox");
    private long cooldown = ConfigManager.getConfig().getLong("Abilities.Plant.ThornyBush.Cooldown");

    private Set<Entity> entitySet = new HashSet<>();

    private List<Thorn> thornList = new ArrayList<>();
    private int abilityState = 0;
    public ThornyBush(Player player) {
        super(player);
        if (CoreAbility.hasAbility(player,getClass()) || !bPlayer.canBendIgnoreBinds(this)) return;
        start();
    }

    public void setThorns(Location startLoc){
        for (int i = 1; i < thorns + 1; i++) {
            double x1 = (1 * Math.sin(Math.toRadians(360.0 / thorns) * i));
            double z1 = (1 * Math.cos(Math.toRadians(360.0 / thorns) * i));
            double x2 = x1 * range;
            double z2 = z1 * range;
            Location spawnLocation = startLoc.clone().add(x1,0,z1);
            spawnLocation.setDirection(GeneralMethods.getDirection(spawnLocation,startLoc.clone().add(x2,0,z2)));

            thornList.add(new Thorn(spawnLocation,this));
        }
    }

    public Location getFloor(Location location){
        int count = 0;
        while (!location.subtract(0,1,0).getBlock().isSolid() && 50 > count++);
        return location.getBlock().getLocation().add(.5,1.2,.5);
    }

    @Override
    public void progress() {
        if (!player.isOnline() || player.isDead()) {
            remove();
            return;
        }else if (GeneralMethods.isRegionProtectedFromBuild(player,player.getLocation())) {
            remove();
            return;
        }
        if (abilityState == 0) {
            if (!player.isSneaking()) {
                remove();
                return;
            } else if (System.currentTimeMillis() - getStartTime() >= 1000) {
                abilityState++;
            }
        } else if (abilityState == 1) {
            if (!player.isSneaking()) {
                setThorns(getFloor(GeneralMethods.getTargetedLocation(player,sourceRange)).add(0,1,0));
                abilityState++;
            } else {
                final Location eyeLocation = player.getEyeLocation().add(player.getLocation().getDirection());
                player.getWorld().spawnParticle(Particle.BLOCK_DUST,eyeLocation,1,0,0,0, Material.ROSE_BUSH.createBlockData());
                eyeLocation.getWorld().playSound(eyeLocation, Sound.BLOCK_SWEET_BERRY_BUSH_BREAK,.5f,1);
            }
        } else if (abilityState == 2) {
            if (thornList.isEmpty()){
                remove();
                return;
            }

            thornList.removeIf(Thorn::progress);
        }

    }


    @Override
    public void remove() {
        super.remove();
        bPlayer.addCooldown(this);
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
        return "ThornyBush";
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


    public void doKnockback(Location location, Location origin, double wave){
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location,2)){
            if (entity instanceof LivingEntity && (entity instanceof Player || !(entity instanceof Monster))){
                LivingEntity livingEntity = (LivingEntity) entity;

                Vector entityDirection = entity.getLocation().getDirection();
                Vector entityToPlayer = origin.toVector().subtract(entity.getLocation().toVector()).normalize();

                double dotProduct = entityDirection.dot(entityToPlayer);

                double desiredSpeed;

                double angle = Math.toDegrees(Math.acos(dotProduct));

                if (angle < 90) {
                    desiredSpeed = -wave/particlesPerCycle;
                } else {
                    desiredSpeed = wave/particlesPerCycle;
                }

                Vector desiredVelocity = entityDirection.clone().multiply(desiredSpeed);
                livingEntity.setVelocity(desiredVelocity);
            }
        }
    }



    public class Thorn {
        private Location location,origin;
        double amp = ThreadLocalRandom.current().nextDouble(amplitude/2,amplitude);
        double wave = ThreadLocalRandom.current().nextDouble(wavelength/2,wavelength);
        double perio = ThreadLocalRandom.current().nextDouble(period/2,period);
        double x = 0;
        private ThornyBush bush;
        public Thorn(Location location,ThornyBush bush) {
            this.location = location;
            origin = location.clone();
            this.bush = bush;
        }

        public boolean progress() {
            if (x >= range){
                return true;
            }else if (!location.getBlock().isPassable() || GeneralMethods.checkDiagonalWall(location,location.getDirection().setY(0).multiply(wave / particlesPerCycle))) {
                return true;
            }

            createSineWave();
            doDamage();
            return false;
        }

        public void doDamage(){
            final LivingEntity entity = GeneralMethods.getClosestLivingEntity(location,hitbox);
            if (entity == null || entity.equals(player)) return;
            doKnockback(entity.getLocation(),origin,wave);
            if (entitySet.contains(entity)) return;
            DamageHandler.damageEntity(entity,player,damage,bush);
            entitySet.add(entity);
        }


        public void createSineWave() {
            final Vector toDirection = location.getDirection().setY(0);
            double z = amp * Math.sin((2 * Math.PI * x / wave) - (2 * Math.PI / perio));
            Location sinOffset = location.clone();
            sinOffset.setY(location.clone().getY() + z);
            player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY,sinOffset,1,0.25,0,0.25,1);
            if (getCurrentTick() % 5 == 0){
                player.getWorld().playSound(sinOffset,Sound.BLOCK_VINE_PLACE,.1f,.5f);
                player.getWorld().spawnParticle(Particle.BLOCK_DUST,sinOffset,1,0.25,0,0.25,0, Material.SPRUCE_LEAVES.createBlockData());
            }else{
                player.getWorld().spawnParticle(Particle.FALLING_DUST,sinOffset,1,0.25,0,0.25,0., Material.BIRCH_LEAVES.createBlockData());
                player.getWorld().spawnParticle(Particle.FALLING_DUST,sinOffset,1,0.25,0,0.25,0., Material.GREEN_TERRACOTTA.createBlockData());
                player.getWorld().spawnParticle(Particle.FALLING_DUST,sinOffset,1,0.25,0,0.25,0., Material.RED_CONCRETE.createBlockData());
            }
            location.add(toDirection.multiply(wave / particlesPerCycle));
            x += wave / particlesPerCycle;
        }
    }

    public String getInstructions(){
        return "Hold Sneak until you see particles, then un-sneak to start the move!";
    }

    public String getDescription(){
        return "This ability conjures a thorny bush at the targeted location. The thorny bush obstructs movement and damages entities that come into contact with it, making it an effective defensive and offensive tool.";
    }
}
