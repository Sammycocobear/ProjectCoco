package me.scb.Abilities.Water.Blood;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.BloodAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import me.scb.Abilities.Water.Blood.BloodUtils.SourceAnimation;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import me.scb.Utils.FallDamageRemoval;
import me.scb.Utils.RainbowColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;

public class BloodBlink extends BloodAbility implements AddonAbility {
    private final static PotionEffect invis = new PotionEffect(PotionEffectType.INVISIBILITY, 2, 0, false, false, false);
    private final static PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 2,  ConfigManager.getConfig().getInt("Abilities.Blood.BloodBlink.SpeedAmplifier"), false, false, false);
    private final static ThreadLocalRandom random = ThreadLocalRandom.current();
    private SourceAnimation s;
    private final double range = ConfigManager.getConfig().getDouble("Abilities.Blood.BloodBlink.SourceRange");
    private final long duration = ConfigManager.getConfig().getLong("Abilities.Blood.BloodBlink.Duration");
    private final long cooldown = ConfigManager.getConfig().getLong("Abilities.Blood.BloodBlink.Cooldown");
    private final double velocity = ConfigManager.getConfig().getDouble("Abilities.Blood.BloodBlink.JumpVelocity");


    private int index = 0;
    public BloodBlink(Player player) {
        super(player);
        if (CoreAbility.hasAbility(player,getClass()) || !bPlayer.canBend(this)) return;
        final Entity target = GeneralMethods.getTargetedEntity(player,range);

        if (!(target instanceof LivingEntity)) return;
        s = new SourceAnimation((LivingEntity) target,player);

        start();
    }
    int skipTick = 0;
    public void jump(){
        if (!hasStart ||  hasJumped) return;
        hasJumped = true;
        player.setVelocity(player.getLocation().getDirection().multiply(velocity));
        FallDamageRemoval.addFallDamageCap(player,0);
    }


    boolean hasStart = false,hasJumped =false;
    @Override
    public void progress() {
        if (!hasStart && s.createBloodStream()){
            hasStart = true;
            return;
        }else if (player.isDead() || !player.isOnline()){
            remove();
            return;
        }else if (GeneralMethods.isRegionProtectedFromBuild(player,player.getLocation())){
            remove();
            return;
        }

        if (!hasStart) return;
        if (System.currentTimeMillis() - getStartTime() >= duration){
            remove();
            return;
        }
        index++;
        for (double height = 0; height < player.getHeight(); height += 0.25) {
            double rx = random.nextDouble(0.0, player.getWidth());
            double rz = random.nextDouble(0.0, player.getWidth());
            if (RainbowColor.playParticles(player,player.getLocation().add(0,height,0),index,rx,0,rz)) {
                player.getWorld().spawnParticle(
                        Particle.REDSTONE, player.getLocation().add(0.0, height, 0.0), 3,
                        rx, 0.0,
                        rz, new Particle.DustOptions(Color.RED, .8f));
            }
        }


        invis.apply(player);
        speed.apply(player);
        if (hasJumped && player.getLocation().subtract(0,.4,0).getBlock().isSolid() && skipTick++ > 2){
            remove();
            return;
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
        return "BloodBlink";
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
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

    public String getInstructions(){
        return "Left click on an entity to source from them. Left click again to move in any direction you're looking.";
    }

    public String getDescription(){
        return "Hide in your opponents blood by surrounding yourself in a blood cloud created with their own blood.";
    }
}
