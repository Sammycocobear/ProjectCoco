package me.scb.Abilities.Water.Healing;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.HealingAbility;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import me.scb.Utils.RainbowColor;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class RefreshingRain extends HealingAbility implements AddonAbility {
    private int abilityState;
    private final long chargetime = ConfigManager.getConfig().getLong("Abilities.Healing.RefreshingRain.ChargeTime");
    private final long duration = ConfigManager.getConfig().getLong("Abilities.Healing.RefreshingRain.Duration");
    private final long healingDelay = ConfigManager.getConfig().getLong("Abilities.Healing.RefreshingRain.HealingDelay");
    private final double healingPerDelay = ConfigManager.getConfig().getDouble("Abilities.Healing.RefreshingRain.HealingPerDelay");
    private long startLong,next;
    private int index = 0;
    public RefreshingRain(Player player) {
        super(player);
        if (CoreAbility.hasAbility(player,getClass()) || !bPlayer.canBend(this)) return;
        start();
    }

    @Override
    public void progress() {
       if (this.player.isDead() || !player.isOnline()) {
            remove();
            return;
        } else if (GeneralMethods.isRegionProtectedFromBuild(this, player.getLocation())) {
            remove();
            return;
        } else if (!bPlayer.canBendIgnoreBinds(this)) {
            remove();
            return;
        } else {
    if (abilityState == 0) {
        if (!player.isSneaking()) {
            remove();
            return;
        } else if (System.currentTimeMillis() > getStartTime() + chargetime) {
            abilityState++;
        }
        } else if (abilityState == 1) {
            if (!player.isSneaking()) {
                abilityState++;
                startLong = System.currentTimeMillis();
            } else {
                final Location eyeLocation = player.getEyeLocation().add(player.getLocation().getDirection());
                if (player.getEyeLocation().getBlock().getType() == Material.WATER){
                    player.getLocation().getWorld().spawnParticle(Particle.WATER_BUBBLE,eyeLocation,1,0,0,0,0);
                }else{
                    player.getLocation().getWorld().spawnParticle(Particle.END_ROD,eyeLocation,1,0,0,0,0);
                }
            }
        } else if (abilityState == 2) {
        if (System.currentTimeMillis() - startLong >= duration){
            remove();
            return;
        }

        if (System.currentTimeMillis() >= next){
            next = System.currentTimeMillis() + healingDelay;
            player.setHealth(Math.min(player.getHealth() + healingPerDelay,20));
        }

        int particleCount = 12;
        int rand = ThreadLocalRandom.current().nextInt(0, particleCount);
        final Location loc = player.getLocation().add(0, 2, 0);

        double centerX = loc.getX();
        double centerZ = loc.getZ();

        for (int i = 0; i < particleCount; i++) {
            double angle = Math.toRadians(360.0 / particleCount * i);
            double x = centerX + .5 * Math.sin(angle);
            double z = centerZ + .5 * Math.cos(angle);
            Location spawnLocation = new Location(loc.getWorld(), x, loc.getY(), z);
            if (rand == i) {
                player.spawnParticle(Particle.END_ROD, spawnLocation, 1, .2, .2, .2, .1);
            }
            if (i % 3 == 0) {
                player.spawnParticle(Particle.FALLING_WATER, spawnLocation, 1, .2, .2, .2, .1);
            }
            if (RainbowColor.playParticles(player,spawnLocation,index,0,0,0,2)) {
                player.spawnParticle(Particle.CLOUD, spawnLocation, 1, .25, 0, .25, 0);
            }
        }
        index++;


    }

        }
    }

    @Override
    public boolean isSneakAbility() {
        return true;
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
        return "RefreshingRain";
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
