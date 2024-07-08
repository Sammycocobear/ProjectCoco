package me.scb.Abilities.Water.Blood;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.BloodAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import me.scb.Utils.RainbowColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class BloodBlast extends BloodAbility implements AddonAbility {
    private final double range = ConfigManager.getConfig().getDouble("Abilities.Blood.BloodBlast.Range");
    private final double playerDamage = ConfigManager.getConfig().getDouble("Abilities.Blood.BloodBlast.PlayerDamage"),
            damage = ConfigManager.getConfig().getDouble("Abilities.Blood.BloodBlast.Damage");
    private final long cooldown = ConfigManager.getConfig().getLong("Abilities.Blood.BloodBlast.Cooldown"),
            timeUntilDamage = ConfigManager.getConfig().getLong("Abilities.Blood.BloodBlast.TimeUntilDamage");

    private double radius = (timeUntilDamage/1000.0) * 3;
    private final double radiusDecreasePerTick = (radius * 50)/timeUntilDamage;

    private int index = 0;

    private Location startLocation, origin;
    private double speed = ConfigManager.getConfig().getDouble("Abilities.Blood.BloodBlast.Speed");


    public BloodBlast(Player player) {
        super(player);
        if (!bPlayer.canBend(this)){
            return;
        }

        if (CoreAbility.hasAbility(player,getClass())) return;

        //player.sendMessage(radiusDecreasePerTick+"");
        start();
    }

    private void createCircle(){
        int particleCount = 12;
        for (int i = 0; i < particleCount; i++) {
            double x = (radius * Math.sin(Math.toRadians(360.0 / particleCount) * i));
            double z = (radius * Math.cos(Math.toRadians(360.0 / particleCount) * i));
            Location spawnLocation = player.getLocation().add(x, 0, z);
            if (RainbowColor.playParticles(player,spawnLocation,index)) {
                spawnLocation.getWorld().spawnParticle(Particle.REDSTONE, spawnLocation, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 2));
            }else{
                index++;
            }
        }
    }

    public void handleCircleAnimation(){
        //slurping sound............
        radius-= radiusDecreasePerTick;
        createCircle();
        if (radius <= 0){
            if (damage>0){
                DamageHandler.damageEntity(player,playerDamage,this);
            }
            startLocation = player.getEyeLocation();
            origin = startLocation.clone();
            //boom sound
            //flash particle
        }


    }

    public void handleBlast(){
        startLocation.add(startLocation.getDirection().multiply(speed));
        if (startLocation.distanceSquared(origin) >= (range * range)){
            remove();
            return;
        }

        if (RainbowColor.playParticles(player,startLocation,index)) {
            startLocation.getWorld().spawnParticle(Particle.REDSTONE, startLocation, 3, .5, .5, .5, 0, new Particle.DustOptions(Color.RED, 2));
        }else{
            index++;
        }

        if (index % 10 == 0){
            startLocation.getWorld().spawnParticle(Particle.FLASH, startLocation, 2, .5, .5, .5, 0);

        }


    }



    @Override
    public void progress() {
        if (player.isDead() || !player.isOnline()){
            remove();
            return;
        }else if (GeneralMethods.isRegionProtectedFromBuild(player,player.getLocation())){
            remove();
            return;
        }else if (!bPlayer.canBendIgnoreBinds(this)){
            remove();
            return;
        }

        if (radius > 0){
            handleCircleAnimation();
        }else{
            handleBlast();
        }
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
        return "BloodBlast";
    }

    @Override
    public Location getLocation() {
        return startLocation == null ? player.getLocation() : startLocation;
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
}
