package me.scb.Abilities.Air.Sound;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import me.scb.Abilities.Air.Sound.SoundElement.SoundAbility;
import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import me.scb.Utils.RainbowColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SoundBarrier extends SoundAbility implements AddonAbility, PassiveAbility {
    private static final double MAX_SPEED = .75;
    private static final int TICKS_PER_SPEED_INCREASE = 5;
    private static final float acceleration = (float) .02;
    private int runningTime;
    private int tickCounter;
    private boolean blastSound = false;
    private double cosX, sinX, cosY, sinY;
    private double yaw, pitch;
    public SoundBarrier(Player player) {
        super(player);
    }
    double angle;
    private int index = 0;

    @Override
    public void progress() {



        if (!player.isSprinting()) {
            runningTime = 0;
            player.setWalkSpeed(0.2f);
            tickCounter = 0;
            blastSound = false;
            return;
        }

        runningTime++;
        tickCounter++;
        if (tickCounter >= TICKS_PER_SPEED_INCREASE) {
            double speed = Math.min(MAX_SPEED, player.getWalkSpeed() + acceleration);
            player.setWalkSpeed((float) speed);
            tickCounter = 0;

        }

        if (player.getWalkSpeed() == MAX_SPEED) {
            if (!blastSound) {
                player.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2, 1);
                blastSound = true;
            }
            yaw = Math.toRadians(player.getLocation().getYaw() + 180);
            pitch = Math.toRadians(player.getLocation().getPitch() - 90);

            cosX = Math.cos(pitch);
            sinX = Math.sin(pitch);
            cosY = Math.cos(-yaw);
            sinY = Math.sin(-yaw);
            Location playerLoc = player.getLocation().subtract(player.getLocation().getDirection().multiply(.5)).add(0,1.5,0);
            for (int i = 0; i < 25; i += 1) {
                double x = (-i * Math.cos(angle));
                double y = (-i);
                double z = (-i * Math.sin(angle));
                Vector vector = new Vector(x, y, z);
                vector = rotateAroundAxisX(vector, cosX, sinX);
                vector = rotateAroundAxisY(vector, cosY, sinY);
                vector.multiply(.08);
                playerLoc.add(vector);
                angle += .08;
                if (RainbowColor.playParticles(player,playerLoc,index)) {
                    player.getLocation().getWorld().spawnParticle(Particle.CLOUD, playerLoc, 1, 0, 0, 0, 0);
                }
                playerLoc.subtract(vector);
            }
            index++;
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(player.getLocation().add(0,.5,0),2)){
                if (AbilityUtils.isInValidEntity(entity,player)) continue;
                DamageHandler.damageEntity(entity,player,1,this);
                player.setWalkSpeed(.2f);
            }
        }

        if (player.isDead() || !player.isValid()) {
            return;
        }

    }

    @Override
    public void remove() {
        super.remove();
        player.setWalkSpeed(.2f);
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
        return "SoundBarrier";
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

    @Override
    public boolean isInstantiable() {
        return true;
    }

    @Override
    public boolean isProgressable() {
        return true;
    }

    private Vector rotateAroundAxisX(Vector v, double cos, double sin) {
        double y = v.getY() * cos - v.getZ() * sin;
        double z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    private Vector rotateAroundAxisY(Vector v, double cos, double sin) {
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }
}
