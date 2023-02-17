package me.scb.Abilities.Fire.Lightning;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.LightningAbility;
import de.slikey.effectlib.effect.LineEffect;
import me.scb.ProjectCoco;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Railgun extends LightningAbility implements AddonAbility {
    private long minChargeTime = 500;
    private long maxChargeTime = 2500;
    public Railgun(Player player) {
        super(player);
        start();
    }
    public boolean canShoot(){
        return System.currentTimeMillis() - getStartTime() >= minChargeTime;
    }


    double chargePercentage = 0;
    public void updateChargeDisplay(Player player) {
        long elapsedTime = System.currentTimeMillis() - getStartTime();
        if (elapsedTime <= maxChargeTime){
            chargePercentage = (double) elapsedTime / (double) maxChargeTime;
        }

        String chargeMessage = canShoot() ? String.format(ChatColor.BLUE + "Ready to release... %d%%", (int) (chargePercentage * 100) + 1)
                : String.format(ChatColor.RED + "Charging... %d%%", (int) (chargePercentage * 100) + 1);

        player.sendActionBar(chargeMessage);
    }

    private Location getRandomLocation(){
        return player.getLocation().add(new Vector(ThreadLocalRandom.current().nextDouble(-1,1) * 5,ThreadLocalRandom.current().nextDouble() * 5,ThreadLocalRandom.current().nextDouble(-1,1) * 5));
    }
    List<ZigZag> zigZags = new ArrayList<>();

    private void zigZag(Location location,double speed) {
        zigZags.add(new ZigZag(location,player,speed));
    }


    private void zigZag() {
        zigZags.add(new ZigZag(getRandomLocation(),player));
    }


    boolean hasShot = false;
    int c = 0;
    @Override
    public void progress() {
        if (System.currentTimeMillis() - getStartTime() >= maxChargeTime + 2000){
            remove();
            return;
        }
        if (player.isSneaking() && !hasShot){
            updateChargeDisplay(player);
            if (c++ == 8) {
                for (int i = 0; i < (int) (Math.random() * 3) + 1; i++) {
                    zigZag();
                }
                player.sendMessage("made");
                c = 0;
            }
        }else if (canShoot() && !hasShot) {
            hasShot = true;
            zigZags.clear();
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
            player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, player.getLocation(), 1);
            player.getWorld().spawnParticle(Particle.SPELL_INSTANT, player.getLocation(), 50, 0.5, 0.5, 0.5);
            double chargePercentage = (double)(System.currentTimeMillis() - getStartTime()) / (double)(maxChargeTime);
            for (int i = 0; i < 5; i++) {
                zigZag(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(-3)).add(Math.random() *  5,Math.random() *  10,Math.random() *  5),  .5);
            }
            Location location = player.getEyeLocation();
            location.setPitch(-1);
            Vector direction = location.getDirection();
            direction.multiply(chargePercentage * 15.0);
            player.setVelocity(direction);
            player.getWorld().spawnParticle(Particle.SMOKE_LARGE, player.getLocation(), 1);
            player.getWorld().spawnParticle(Particle.CRIT_MAGIC, player.getLocation(), 5);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        }else if (zigZags.isEmpty()){
            remove();
            return;
        }
        if (hasShot && c++ == 15){
            zigZag(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(-3)).add(Math.random() *  5,Math.random() *  10,Math.random() *  5),  .5);
            c = 0;
        }
        zigZags.removeIf(ZigZag::zigZag);




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
        return "Railgun";
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
