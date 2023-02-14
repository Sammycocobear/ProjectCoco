package me.scb.Abilities.Fire.Combustion;

import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CombustionAbility;
import me.scb.Utils.AbilityUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CombustionBomb extends CombustionAbility implements AddonAbility {
    private Location location;
    private Vector direction;
    private double gravity = .05;
    private double speed = .5;
    private int checks = 10;
    private int bounces = 0;
    private boolean goingDown = true;
    private Location lastBounceLocation;
    private double maxHeight = 1;

    public CombustionBomb(Player player) {
        super(player);
        location = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(1));
        direction = getDirection().multiply(speed);
        start();
    }

    @Override
    public void progress() {
        if (System.currentTimeMillis() - getStartTime() >= 5000){
            remove();
            return;
        }
        if (!location.getBlock().isPassable() && goingDown){
            if (location.getBlock().getRelative(BlockFace.UP).isSolid()){
                remove();
                return;
            }
            bounces++;
            goingDown = false;
            location = location.getBlock().getRelative(BlockFace.UP).getLocation().add(0.5,.5,.5);
            lastBounceLocation = location.clone();

            direction = getDirection().multiply(speed);
        }else if (!goingDown && location.getY() - lastBounceLocation.getY() >= maxHeight){
            goingDown = true;
        }
        if (goingDown){
            bounceDown();
        }else{
            bounceUp();
        }

        makeSphere();
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

    public void makeSphere(){
        int particles = 10;
        double radius = .5;

        for (int i = 0; i < particles; i++) {
            Vector vector = AbilityUtils.getRandomVector().multiply(radius);
            location.add(vector);
            location.getWorld().spawnParticle(Particle.FLAME,location,1,0,0,0,0);
            location.subtract(vector);
        }
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
