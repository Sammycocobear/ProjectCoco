package me.scb.Abilities.Fire.Combustion;

import com.google.gson.JsonArray;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CombustionAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import me.scb.Utils.AbilityUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class CombustionBomb extends CombustionAbility implements AddonAbility {
    private Location location;
    private Vector direction;
    //TODO ADD CONFIG
    private double gravity = .05;
    private double speed = .25;
    private int bounces = 0;
    private boolean goingDown = true;
    private Location lastBounceLocation;
    private double maxHeight = 3;
    private boolean burst,clickBurst = false;
    private int maxBounces = 3;
    private double circleRadius,circleSpeed = .5,maxCircleRadius = 5;
    private double hitbox = 1;
    private int maxSpheres = 6;
    private Location[] locations = new Location[maxSpheres];
    private List<Entity> entityList = new ArrayList<>();
    private double damage = 1;
    public CombustionBomb(Player player) {
        super(player);
        location = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(1));
        direction = getDirection().multiply(speed);
        start();
    }

    @Override
    public void progress() {
        if (!burst) {
            checkLocation();
            bounce();
            makeSphere(location);
            Entity nearby = getNearbyEntity(location);
            if (nearby != null){
                setBurst(nearby.getLocation());
            }
            if (!location.getBlock().isPassable()){
                if (location.getBlock().getRelative(BlockFace.UP).isSolid()) {
                    remove();
                    return;
                }
            }
        }else{
            makeBurst();
        }

    }

    public void explode(){
        for (int i = 0; i < maxSpheres; i++) {
            if (brokenSpheres[i]) continue;
            Location l = locations[i];
            l.getWorld().spawnParticle(Particle.EXPLOSION_LARGE,l,5,0,0,0,0);
            l.getWorld().playSound(l,Sound.ENTITY_GENERIC_EXPLODE,1,1);

        }
    }

    boolean[] brokenSpheres = new boolean[maxSpheres];


    public void makeBurst(){
        for (int i = 0; i < maxSpheres; i++) {
            double x = (circleRadius * Math.sin(Math.toRadians(360.0/ maxSpheres) * i));
            double z = (circleRadius * Math.cos(Math.toRadians(360.0/ maxSpheres) * i));
            if (brokenSpheres[i]) continue;
            locations[i] = location.add(x,0,z).clone();
            if (!location.getBlock().isPassable()){
                brokenSpheres[i] = true;
            }
            doDamage(location);
            makeSphere(location);
            location.subtract(x,0,z);
        }
        if (circleRadius >= maxCircleRadius){
            explode();
            remove();
            return;
        }
        circleRadius+=circleSpeed;
    }

    public Entity getNearbyEntity(Location location){
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location,hitbox)){
            if (AbilityUtils.isInValidEntity(entity,player)) continue;
            return entity;
        }
        return null;
    }

    public void doDamage(Location location){
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location,hitbox)){
            if (AbilityUtils.isInValidEntity(entity,player) || entityList.contains(entity)) continue;
            DamageHandler.damageEntity(entity,player,damage,this);
            entityList.add(entity);
        }
    }

    public void bounce(){
        if (goingDown) {
            bounceDown();
        } else {
            bounceUp();
        }
    }


    public void checkLocation(){
        if (!location.getBlock().isPassable() && goingDown) {
            if (bounces++ == maxBounces || clickBurst){
                setBurst(location.getBlock().getRelative(BlockFace.UP).getLocation().add(0.5,1,.5));
                return;
            }
            goingDown = false;
            location = location.getBlock().getRelative(BlockFace.UP).getLocation().add(0.5, .5, .5);
            lastBounceLocation = location.clone();

            direction = getDirection().multiply(speed);
        } else if (!goingDown && location.getY() - lastBounceLocation.getY() >= maxHeight) {
            goingDown = true;

        }
    }


    public Vector getDirection(){
        Location location = player.getEyeLocation();
        location.setPitch(0);
        return location.getDirection();
    }

    private void bounceUp() {
        direction = direction.add(new Vector(0, gravity, 0));
        location.add(direction);
    }

    public void makeSphere(Location location){
        int particles = 10;
        double radius = .5;

        for (int i = 0; i < particles; i++) {
            Vector vector = AbilityUtils.getRandomVector().multiply(radius);
            location.add(vector);
            location.getWorld().spawnParticle(Particle.FLAME,location,1,0,0,0,0);
            location.subtract(vector);
        }
    }

    //TODO use for click
    public void setClickBurst(){
        if (clickBurst) return;
        this.clickBurst = true;
    }

    public void setBurst(Location location){
        if (burst) return;
        burst = true;
        this.location = location.clone();
    }

    public void bounceDown(){
        direction = direction.subtract(new Vector(0, gravity, 0));
        location.add(direction);
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
        return "CombustionBomb";
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
