package me.scb.Abilities.Fire.BlueFire;

import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.BlueFireAbility;
import de.slikey.effectlib.util.MathUtils;
import me.scb.Configuration.ConfigManager;
import me.scb.Utils.AbilityUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BlueFireOrbs extends BlueFireAbility implements AddonAbility {
    private final long cooldown = ConfigManager.getConfig().getLong("Abilities.BlueFire.BlueFireOrbs.Cooldown");
    private final int maxOrbs = Math.max(ConfigManager.getConfig().getInt("Abilities.BlueFire.BlueFireOrbs.OrbCount"),1);
    private final double range = ConfigManager.getConfig().getDouble("Abilities.BlueFire.BlueFireOrbs.OrbRange");
    private final boolean isControllable = ConfigManager.getConfig().getBoolean("Abilities.BlueFire.BlueFireOrbs.IsControllable");
    private final double radius = ConfigManager.getConfig().getDouble("Abilities.BlueFire.BlueFireOrbs.Radius");
    private final double speed = ConfigManager.getConfig().getDouble("Abilities.BlueFire.BlueFireOrbs.Speed");
    private boolean isShooting = false;
    List<Orb> orbs = new ArrayList<>();
    private boolean[] shot = new boolean[maxOrbs];
    public BlueFireOrbs(Player player) {
        super(player);
        final Location location = player.getLocation();
        final double angleIncrement = 360.0 / maxOrbs;
        for (int i = 0; i < maxOrbs; i++) {
            if (shot[i]) continue;
            double x = radius * Math.sin(Math.toRadians(angle));
            double z = radius * Math.cos(Math.toRadians(angle));
            location.add(x, 0, z);
            orbs.add(new Orb(location.clone()));
            location.subtract(x, 0, z);
            angle = ((angle + angleIncrement) % 360.0) + speed;
        }


        start();
    }

    public void makeSphere(Location location){
        //instance variables probably
        for (int i = 0; i < 5; i++) {
            location.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME,location.clone().add(AbilityUtils.getRandomVector().multiply(.5)),1,0,0,0, ThreadLocalRandom.current().nextBoolean() ? 0 : .05);
        }
    }

    public Orb getFirstActiveOrb(){
        for (int i = 0; i < orbs.size(); i++) {
            Orb orb = orbs.get(i);
            if (orb.getActive()){
                return orb;
            }
        }
        return null;
    }

    public Orb getClosest(){
        final Location infront = player.getLocation().add(0, 1, 0).add(player.getLocation().getDirection().multiply(radius));
        double closestDistance = 0;
        int count = 0;
        for (int i = 0; i < orbs.size(); i++) {
            Orb orb = orbs.get(i);
            if (orb.getActive()){
                count++;
                closestDistance = orb.getLocation().distanceSquared(infront);
            }
        }
        if (count == 1){
            return orbs.get(0);
        }

        Orb closestOrb = null;
        for (Orb orb : orbs){
            if (!orb.getActive()) continue;
            Location l = orb.getLocation();
            double distance = l.distanceSquared(infront);
            if (closestDistance > distance) {
                closestOrb = orb;
                closestDistance = distance;
            }
        }
        return closestOrb;
    }

    double angle = 0;

    public void setShoot(){
        if (isShooting) return;
        isShooting = true;
        getClosest().setShoot(true);
    }


    public void visuals() {
        final Location location = player.getLocation().add(0, 1, 0);
        final double angleIncrement = 360.0 / maxOrbs; // calculate angle increment for each point
        double speed = .25;
        for (int i = 0; i < orbs.size(); i++) {
            Orb orb = orbs.get(i);
            if (orb.isShooting()) continue;
            double x = radius * Math.sin(Math.toRadians(angle));
            double z = radius * Math.cos(Math.toRadians(angle));
            location.add(x, 0, z);
            orbs.get(i).setLocation(location.clone());
            location.subtract(x, 0, z);
            angle = ((angle + angleIncrement) % 360.0) + speed;
        }


    }





    @Override
    public void progress() {
        if (System.currentTimeMillis() - getStartTime() >= 10000){
            remove();
            return;
        }
        orbs.removeIf(Orb::run);

        visuals();


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
        return "BlueFireOrbs";
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

    public class Orb{

        private Location location;
        private Location shotOrigin = null;
        private boolean isActive = true, isShoot = false;
        public Orb(Location location){
            this.location = location;
            run();
        }

        public boolean run(){
            if (!getActive()) return true;
            if (isShooting()){
                if (shoot()){
                    setActive(false);
                    setShoot(false);
                    isShooting = false;
                    player.sendMessage(isActive + " ");
                    return true;
                }
            }
            makeSphere(location);
            return false;
        }

        public void setActive(boolean active){
            this.isActive = active;
        }

        public boolean getActive(){
            return isActive;
        }

        public void setShoot(boolean shot){
            shotOrigin = location.clone();
            this.isShoot = shot;
        }

        public boolean isShooting(){
            return isShoot;
        }

        public void setLocation(Location newLocation){
            this.location = newLocation;
        }

        public Location getLocation(){
            return location;
        }


        public boolean shoot(){
            if (location.distanceSquared(shotOrigin) >= (range * range)){
                return true;
            }

            location.add(isControllable ? player.getLocation().getDirection().multiply(speed) : shotOrigin.getDirection().multiply(speed));
            makeSphere(location);
            return false;
        }


    }
}
