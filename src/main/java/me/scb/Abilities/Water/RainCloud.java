package me.scb.Abilities.Water;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.WaterAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import de.slikey.effectlib.effect.CircleEffect;
import de.slikey.effectlib.effect.CloudEffect;
import de.slikey.effectlib.util.RandomUtils;
import me.scb.Abilities.Fire.Lightning.ZigZag;
import me.scb.Abilities.Water.Ice.Hail;
import me.scb.Configuration.ConfigManager;
import me.scb.Utils.AbilityUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.List;

public class RainCloud extends WaterAbility implements AddonAbility {
    private final double radius = ConfigManager.getConfig().getDouble("Abilities.Water.RainCloud.Radius");
    private final double sourceRange = ConfigManager.getConfig().getDouble("Abilities.Water.RainCloud.SourceRange");
    private final long cooldown = ConfigManager.getConfig().getLong("Abilities.Water.RainCloud.Cooldown");
    private final double height = ConfigManager.getConfig().getDouble("Abilities.Water.RainCloud.Height");
    private Location location, floorLocation;
    private boolean isHailing = false;
    private long nextDamage = 0,damageInterval = ConfigManager.getConfig().getLong("Abilities.Ice.Hail.Damage");
    private double damage = ConfigManager.getConfig().getDouble("Abilities.Ice.Hail.Damage");
    private int slownessDuration = ConfigManager.getConfig().getInt("Abilities.Ice.Hail.SlownessDuration"),
            slownessAmplifier = ConfigManager.getConfig().getInt("Abilities.Ice.Hail.SlownessAmplifier");
    private Hail hailInstance;
    private boolean applyBoneMeal = true;
    private List<Block> blockList;
    public RainCloud(Player player) {
        super(player);
        if (CoreAbility.hasAbility(player,getClass()) || !bPlayer.canBend(this)) return;
        location = GeneralMethods.getTargetedLocation(player,sourceRange);
        int search = 0;
        while (!location.subtract(0,1,0).getBlock().isSolid() && search++ < 50);
        floorLocation = location.clone().add(0,1,0);
        location.add(0,height + 1,0);
        blockList = GeneralMethods.getBlocksAroundPoint(floorLocation,radius - 1);
        start();
    }

    public void setHailing(boolean state, Hail instance) {
        this.isHailing = state;
        this.hailInstance = instance;
    }

    public void makeVisuals(){
        for (int i = 0; i < 20; i++) {
            Vector v = RandomUtils.getRandomCircleVector().multiply(RandomUtils.random.nextDouble() * radius - 1);
            location.add(v);
            if (isHailing){
                location.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, location.clone().subtract(0,height/2.0,0), 1,0,(height/2.0) - 1.5, 0,0);
            }else{
                location.getWorld().spawnParticle(Particle.FALLING_WATER, location, 1,0,0, 0,0);
            }
            location.subtract(v);
        }
    }

    public void makeCloud(){
        for (int i = 0; i < 20; i++) {
            Vector v = RandomUtils.getRandomCircleVector().multiply(RandomUtils.random.nextDouble() * radius);
            location.getWorld().spawnParticle(Particle.CLOUD, location.add(v), 1,0,0, 0,0);
            location.subtract(v);
        }
    }


    public void doHailDamage(){
        if (System.currentTimeMillis() >= nextDamage){
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(floorLocation.clone().add(0,height/2,0),radius/1.5)){
                if (AbilityUtils.isInValidEntity(entity,player)) continue;
                DamageHandler.damageEntity(entity,player,damage,hailInstance);
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 2));

            }
            nextDamage = System.currentTimeMillis() + damageInterval;
        }

    }

    public void doRain(){
        for (Block b : GeneralMethods.getBlocksAroundPoint(floorLocation,radius - 1)){
            if (b.getType() == Material.FARMLAND){
                Farmland f = (Farmland) b.getBlockData();
                f.setMoisture(f.getMaximumMoisture());
                b.setBlockData(f);
            }else if (applyBoneMeal && Tag.CROPS.getValues().contains(b)){
                b.applyBoneMeal(BlockFace.UP);
            }
        }

    }

    @Override
    public void progress() {
        if (System.currentTimeMillis() - getStartTime() >= 5000){
            remove();
            return;
        }
        makeCloud();
        makeVisuals();
        if (isHailing){
            doHailDamage();
        }else{
            doRain();
        }
    }

    public static List<RainCloud> getRainCloudsInArea(Player player, double distance){
        List<RainCloud> returnList = new ArrayList<>();
        for (CoreAbility ability : RainCloud.getAbilitiesByInstances()){
            if (!ability.getName().equals("RainCloud")) continue;
            if (ability.getLocation().distanceSquared(player.getLocation()) < distance * distance){
                returnList.add((RainCloud) ability);
            }
        }
        return returnList;
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
        return cooldown;
    }

    @Override
    public String getName() {
        return "RainCloud";
    }

    @Override
    public Location getLocation() {
        return location;
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
