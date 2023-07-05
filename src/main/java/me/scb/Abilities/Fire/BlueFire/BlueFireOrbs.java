package me.scb.Abilities.Fire.BlueFire;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.BlueFireAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BlueFireOrbs extends BlueFireAbility implements AddonAbility {
    private final int maxOrbs = Math.max(ConfigManager.getConfig().getInt("Abilities.BlueFire.BlueFireOrbs.OrbCount"),1);
    private final double range = ConfigManager.getConfig().getDouble("Abilities.BlueFire.BlueFireOrbs.OrbRange");
    private final boolean isControllable = ConfigManager.getConfig().getBoolean("Abilities.BlueFire.BlueFireOrbs.IsControllable");
    private final double radius = ConfigManager.getConfig().getDouble("Abilities.BlueFire.BlueFireOrbs.Radius");
    private final double speed = ConfigManager.getConfig().getDouble("Abilities.BlueFire.BlueFireOrbs.Speed");
    private final double damage = ConfigManager.getConfig().getDouble("Abilities.BlueFire.BlueFireOrbs.Radius");
    private final double hitbox = ConfigManager.getConfig().getDouble("Abilities.BlueFire.BlueFireOrbs.Speed");
    private boolean isShooting = false;
    List<Orb> orbs = new ArrayList<>();
    public BlueFireOrbs(Player player) {
        super(player);
        if (CoreAbility.hasAbility(player,getClass()) || !bPlayer.canBend(this)) return;
        final Location location = player.getLocation();
        final double angleIncrement = 360.0 / maxOrbs;
        for (int i = 0; i < maxOrbs; i++) {
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
        for (int i = 0; i < 5; i++) {
            location.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME,location.clone().add(AbilityUtils.getRandomVector().multiply(.5)),1,0,0,0, ThreadLocalRandom.current().nextBoolean() ? 0 : .05);
        }
    }

    public Orb getFirstActiveOrb(){
        for (int i = 0; i < maxOrbs; i++) {
            Orb orb = orbs.get(i);
            if (orb.getActive()){
                return orb;
            }
        }
        return null;
    }

    public Orb getClosest(){ //This is technically doing one unnecessary distance check but idt its a huge deal since its not being called every tick.
        final Location infront = player.getLocation().add(0, 1, 0).add(player.getLocation().getDirection().multiply(radius/2));
        Orb closestOrb = getFirstActiveOrb();
        if (closestOrb == null){
            remove();
            return null;
        }
        double close = closestOrb.getLocation().distanceSquared(infront);
        for (int i = 0; i < maxOrbs; i++) {
            Orb checkOrb = orbs.get(i);
            if (checkOrb.isShooting() || !checkOrb.getActive()) continue;
            double dist = checkOrb.getLocation().distanceSquared(infront);
            if (dist < close){
                close = dist;
                closestOrb = checkOrb;
            }
        }
        return closestOrb;
    }

    public void doDamage(Location location){
        final LivingEntity entity = GeneralMethods.getClosestLivingEntity(location,hitbox);
        if (entity != null && !entity.equals(player)) {
            DamageHandler.damageEntity(entity,player,damage,this);
        }
    }

    double angle = 0;

    public void setShoot(){
        if (isShooting) return;
        isShooting = true;
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SHROOMLIGHT_PLACE,.5f,2f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE,.25f,1f);

        Orb closest = getClosest();
        if (closest == null){
            remove();
            return;
        }
        closest.setShoot(true);
        closest.getLocation().getWorld().spawnParticle(Particle.FLASH,closest.getLocation(),1,0,0,0);
    }


    public void visuals() {
        final Location location = player.getLocation().add(0, 1, 0);
        final double angleIncrement = 360.0 / maxOrbs; //angle increment for each point
        double speed = .25;

        for (int i = 0; i < maxOrbs; i++) {
            Orb orb = orbs.get(i);
            angle = ((angle + angleIncrement) % 360.0) + speed;
            if (orb.isShooting() || !orb.getActive()) {
                continue;
            }
            double x = radius * Math.sin(Math.toRadians(angle));
            double z = radius * Math.cos(Math.toRadians(angle));
            location.add(x, 0, z);
            orb.setLocation(location.clone());
            location.subtract(x, 0, z);
        }


    }

    public String getInstructions(){
        return "Sneak to create the orbs and then click to shoot them.";
    }

    public String getDescription(){
        return "While sneaking, you can create a ring of orbs that will orbit around you. To shoot an orb, simply left-click. The orb will inflict damage upon impact.";
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

        boolean shouldRemove = true;
        for (Orb orb : orbs) {
            if (!orb.run()){
                shouldRemove = false;
            }
        }

        if (shouldRemove){
            remove();
            return;
        }

        visuals();


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
        return ConfigManager.getConfig().getLong("Abilities.BlueFire.BlueFireOrbs.Cooldown");
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
        return ProjectCoco.getAuthor();
    }

    public String getVersion() {
        return ProjectCoco.getVersion();
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
                    this.isActive = false;
                    isShooting = false;
                    return true;
                }
            }
            makeSphere(location);
            return false;
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
            doDamage(location);
            location.add(isControllable ? player.getLocation().getDirection().multiply(speed) : shotOrigin.getDirection().multiply(speed));
            makeSphere(location);
            return false;
        }


    }
}
