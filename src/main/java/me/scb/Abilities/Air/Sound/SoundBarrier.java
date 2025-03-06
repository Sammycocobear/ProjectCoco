package me.scb.Abilities.Air.Sound;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import me.scb.Abilities.Air.Sound.SoundElement.SoundAbility;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import me.scb.Utils.RainbowColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SoundBarrier extends SoundAbility implements AddonAbility, PassiveAbility {
    private final double MAX_SPEED = ConfigManager.getConfig().getDouble("Abilities.Sound.SoundBarrier.MaxSpeed");
    private final int TICKS_PER_SPEED_INCREASE = ConfigManager.getConfig().getInt("Abilities.Sound.SoundBarrier.SpeedIncreaseTimeInTicks");
    private final float acceleration = (float)   ConfigManager.getConfig().getDouble("Abilities.Sound.SoundBarrier.Acceleration");
    private final double damage = ConfigManager.getConfig().getDouble("Abilities.Sound.SoundBarrier.Damage");
    private final double hitbox = ConfigManager.getConfig().getDouble("Abilities.Sound.SoundBarrier.Hitbox");

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
    public boolean isEnabled() {
        return false;
    }

    //TODO make stance move, have players overheat from sspeed, make the max speed have duration, if they dont hit anoyne they get slow, speed gets cancelled when you get hit
    @Override
    public void progress() {
        if (!player.isSprinting() || player.isSneaking()) {
            player.setWalkSpeed(0.2f);
            tickCounter = 0;
            blastSound = false;
            return;
        }

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
            pitch = Math.toRadians(-90);

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
                angle += .15;
                if (RainbowColor.playParticles(player,playerLoc,index,0,0,0,1.5f)) {
                    player.getLocation().getWorld().spawnParticle(Particle.CLOUD, playerLoc, 1, 0, 0, 0, 0);
                }
                playerLoc.subtract(vector);
            }
            index++;
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(player.getLocation().add(0,.5,0),2)){
                if (AbilityUtils.isInValidEntity(entity,player)) continue;
                DamageHandler.damageEntity(entity,player,damage,this);
                player.setWalkSpeed((float) player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue());
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

    public String getInstructions(){
        return "Continuously run until you hear an explosion and particles appear behind you.";
    }

    public String getDescription(){
        return "This passive ability allows you to build up speed while running until you break the sound barrier, enabling you to damage entities you collide with. To use this ability, simply start running and continue until you reach maximum speed. Once you break the sound barrier, any entities you come into contact with will take damage.";
    }

}
