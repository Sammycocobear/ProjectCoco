package me.scb.Abilities.Water.Plant.SeedSummoner;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.PlantAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import me.scb.Configuration.ConfigManager;
import me.scb.Utils.AbilityUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ChiliBean extends PlantAbility{
    private static final Particle.DustOptions dus = new Particle.DustOptions(Color.fromRGB(186, 10, 15),1);
    private Location origin;
    private int firetick;
    private double hitbox,damage;

    private Location location;
    private double amplitude = .5;
    private double wavelength = 3;
    private double period = 20.0;
    private int particlesPerCycle = 20;
    private double x = 0;
    private double range = 10;

    public ChiliBean(Player player) {
        super(player);
        if (CoreAbility.hasAbility(player,getClass()) || !bPlayer.canBend(this)) return;
        this.amplitude = ConfigManager.getConfig().getDouble("Abilities.Plant.SeedSummoner.ChiliBean.Amplitude");
        this.wavelength = ConfigManager.getConfig().getDouble("Abilities.Plant.SeedSummoner.ChiliBean.WaveLength");
        this.period = ConfigManager.getConfig().getInt("Abilities.Plant.SeedSummoner.ChiliBean.Period");
        this.particlesPerCycle = ConfigManager.getConfig().getInt("Abilities.Plant.SeedSummoner.ChiliBean.ParticlesPerCycle");
        this.range = ConfigManager.getConfig().getDouble("Abilities.Plant.SeedSummoner.ChiliBean.Range");
        this.damage = ConfigManager.getConfig().getDouble("Abilities.Plant.SeedSummoner.ChiliBean.Damage");
        this.hitbox = ConfigManager.getConfig().getDouble("Abilities.Plant.SeedSummoner.ChiliBean.Hitbox");
        this.firetick = ConfigManager.getConfig().getInt("Abilities.Plant.SeedSummoner.ChiliBean.FireTicks");



        location = (player.getFacing() == BlockFace.WEST || player.getFacing() == BlockFace.SOUTH) ?
                GeneralMethods.getRightSide(player.getLocation(),wavelength/2) :
                GeneralMethods.getLeftSide(player.getLocation(),wavelength/2);
        location.add(0,.25,0);
        location.setPitch(0);
        origin = location.clone();
        start();
    }

    public Vector perp(Vector onto, Vector u) {
        return u.clone().subtract(proj(onto, u));
    }

    public Vector proj(Vector onto, Vector u) {
        return onto.clone().multiply(onto.dot(u) / onto.lengthSquared());
    }

    public void createSineWave() {
        double beanSize = .2;
        final Vector toDirection = location.getDirection().setY(0);
        double z = amplitude * Math.sin((2 * Math.PI * x / wavelength) - (2 * Math.PI / period));
        Vector perpLine = perp(toDirection, new Vector(1 - Math.abs(toDirection.getX()), 0, 1 - Math.abs(toDirection.getZ()))).normalize().multiply(z);
        location.add(perpLine);
        for (int i = 0; i < 3; i++) {
            Location spawnLocation = GeneralMethods.getLeftSide(location,beanSize-=.2);
            if (i == 0){
                spawnLocation.getWorld().spawnParticle(Particle.FLAME,spawnLocation,1,.1,0,.1,.05);
            }
            for (int j = 0; j < 3; j++) {
                spawnLocation.getWorld().spawnParticle(Particle.REDSTONE,spawnLocation.add(AbilityUtils.getRandomVector().multiply(.2)),1,0,0,0,0,dus);
            }
        }
        location.add(toDirection.multiply(wavelength / particlesPerCycle));
        x += wavelength / particlesPerCycle;
    }


    @Override
    public void progress() {
        if (location.distanceSquared(origin) >= (range * range)) {
            remove();
            return;
        }else if (location.getBlock().isSolid()){
            remove();
            return;
        }else if (player.isDead() || !player.isOnline()) {
            remove();
            return;
        }
        createSineWave();
        final LivingEntity entity = GeneralMethods.getClosestLivingEntity(location,hitbox);
        if (entity != null && entity != player) {
            DamageHandler.damageEntity(entity,player,damage,this);
            entity.setFireTicks(entity.getFireTicks() + firetick);
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
        return 0;
    }

    @Override
    public String getName() {
        return "ChiliBean";
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public boolean isHiddenAbility() {
        return true;
    }

}
