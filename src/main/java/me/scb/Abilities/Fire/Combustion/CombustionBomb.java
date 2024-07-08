package me.scb.Abilities.Fire.Combustion;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CombustionAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import me.scb.Utils.RainbowColor;
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

    private final double gravity = ConfigManager.getConfig().getDouble("Abilities.Combustion.CombustionBomb.Gravity");
    private final double speed = ConfigManager.getConfig().getDouble("Abilities.Combustion.CombustionBomb.Speed");
    private final double circleSpeed = ConfigManager.getConfig().getDouble("Abilities.Combustion.CombustionBomb.EndExplosion.IncreaseSpeed");
    private final double maxCircleRadius = ConfigManager.getConfig().getDouble("Abilities.Combustion.CombustionBomb.EndExplosion.MaxRadius");
    private final double hitbox = ConfigManager.getConfig().getDouble("Abilities.Combustion.CombustionBomb.Hitbox");
    private final double damage = ConfigManager.getConfig().getDouble("Abilities.Combustion.CombustionBomb.Damage");
    private final long cooldown = ConfigManager.getConfig().getLong("Abilities.Combustion.CombustionBomb.Cooldown");
    private final int maxSpheres = ConfigManager.getConfig().getInt("Abilities.Combustion.CombustionBomb.EndExplosion.Spheres");
    private final double maxHeight = ConfigManager.getConfig().getDouble("Abilities.Combustion.CombustionBomb.MaxHeight");
    private final int maxBounces = ConfigManager.getConfig().getInt("Abilities.Combustion.CombustionBomb.MaxBounces");

    private Location location;
    private Vector direction;
    private int bounces = 0;
    private boolean goingDown = true;
    private Location lastBounceLocation;
    private boolean burst,clickBurst = false;
    private double circleRadius;
    private Location[] locations = new Location[maxSpheres];
    private List<Entity> entityList = new ArrayList<>();
    private int index = 0;
    public CombustionBomb(Player player) {
        super(player);
        if (CoreAbility.hasAbility(player,getClass()) || bPlayer.isOnCooldown(this) || !bPlayer.canBend(this)) return;
        location = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(1));
        direction = getDirection().multiply(speed);
        start();
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
        index++;
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
    public boolean allBroken(){
        for (boolean b : brokenSpheres){
            if (!b){
                return false;
            }
        }
        return true;
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
        if (circleRadius >= maxCircleRadius || allBroken()){
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
            if (++bounces == maxBounces || clickBurst){
                setBurst(location.getBlock().getRelative(BlockFace.UP).getLocation().add(0.5,1,.5));
                return;
            }
            player.getWorld().playSound(location, Sound.BLOCK_SHROOMLIGHT_PLACE,.5f,1f);
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
            if (i % 5 < 2){
                location.getWorld().spawnParticle(Particle.SMOKE_NORMAL,location,1,0,0,0,.1);
            }else{
                if (RainbowColor.playParticles(player,location,index)) {
                    if (bPlayer.hasSubElement(Element.BLUE_FIRE)){
                        location.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, location, 1, 0, 0, 0, .05);
                    }else{
                        location.getWorld().spawnParticle(Particle.FLAME, location, 1, 0, 0, 0, .05);
                    }
                }
            }
            location.subtract(vector);
        }
    }

    //TODO use for click might do this
    public void setClickBurst(){
        if (clickBurst) return;
        this.clickBurst = true;
    }

    public void setBurst(Location location){
        if (burst) return;
        burst = true;
        player.getWorld().playSound(location, Sound.BLOCK_SHROOMLIGHT_PLACE,1.5f,1f);
        this.location = location.clone();
    }

    public void bounceDown(){
        direction = direction.subtract(new Vector(0, gravity, 0));
        location.add(direction);
    }

    @Override
    public void remove() {
        super.remove();
        bPlayer.addCooldown(this);
    }

    public String getInstructions(){
        return "Click to summon a CombustionBomb";
    }

    public String getDescription(){
        return "his ability conjures a menacing thunderstorm overhead. As dark clouds gather, occasional lightning strikes cause damage to entities caught within its reach, delivering a shocking impact.";
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
        return ProjectCoco.getAuthor();
    }

    @Override
    public String getVersion() {
        return ProjectCoco.getVersion();
    }
}
