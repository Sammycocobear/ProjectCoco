package me.scb.Abilities.Water.Blood;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.BloodAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import me.scb.Abilities.Water.Blood.BloodUtils.SourceAnimation;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import me.scb.Utils.RainbowColor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BloodPool extends BloodAbility implements AddonAbility {
    private double sourceRange = ConfigManager.getConfig().getDouble("Abilities.Blood.BloodPool.SourceRange");
    private double range = ConfigManager.getConfig().getDouble("Abilities.Blood.BloodPool.Range");
    private int maxHits = ConfigManager.getConfig().getInt("Abilities.Blood.BloodPool.MaxHits");
    private double damage = ConfigManager.getConfig().getDouble("Abilities.Blood.BloodPool.Damage");
    private long cooldown = ConfigManager.getConfig().getLong("Abilities.Blood.BloodPool.Cooldown");
    private double radius = ConfigManager.getConfig().getDouble("Abilities.Blood.BloodPool.Radius");
    private int maxPools = ConfigManager.getConfig().getInt("Abilities.Blood.BloodPool.MaxPools");
    private long damageDelay = ConfigManager.getConfig().getLong("Abilities.Blood.BloodPool.DamageDelay");
    private int poolCount = 0;
    private List<Pool> pools = new ArrayList<>(); //could jus use an array but its easier and im lazy so its wtv
    private Map<Entity,Integer> entityDamageMap = new HashMap<>();
    private Map<Entity, Long> entityDelayMap = new HashMap<>();
    private SourceAnimation s;
    private boolean hasStarted = false, hasMadePool = false;
    private int index;
    private int poolRotation = 0;
    public BloodPool(Player player) {
        super(player);
        if (CoreAbility.hasAbility(player,getClass()) || !bPlayer.canBend(this)) return;
        final Entity target = GeneralMethods.getTargetedEntity(player,sourceRange);

        if (!(target instanceof LivingEntity)) return;
        s = new SourceAnimation((LivingEntity) target,player);


        start();
    }

    public Location getFloor(Location location){
        int count = 0;
        while (!location.subtract(0,1,0).getBlock().isSolid() && 50 > count++);
        return location.getBlock().getLocation().add(.5,1.2,.5);
    }

    public void selectPool(){
        if (!hasStarted || poolCount >= maxPools) return;
        Location location = GeneralMethods.getTargetedLocation(player,range);
        pools.add(new Pool(getFloor(location),System.currentTimeMillis()));
        hasMadePool = true;
        poolCount++;

        player.sendActionBar(ChatColor.RED + "Pools Left: " + (maxPools - poolCount));
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
        if (System.currentTimeMillis() - getStartTime() >= 5000 && !hasMadePool){
            remove();
            return;
        }

        if (!hasStarted && s.createBloodStream()){
            hasStarted = true;
            return;
        }

        if (hasStarted){
            if (RainbowColor.playParticles(player,player.getEyeLocation(),index,.5,.5,.5,1))
                player.getWorld().spawnParticle(Particle.REDSTONE,player.getEyeLocation(),5,.5,.5,.5,0,new Particle.DustOptions(Color.RED,1));
            index++;
        }


        if (hasMadePool){
            poolRotation++;

            pools.removeIf(Pool::makePool);
            if (pools.isEmpty()){
                remove();
                return;
            }
        }

    }

    private void doDamage(Location loc, double radius){
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(loc,radius)){
            if (entityDamageMap.getOrDefault(entity,0) >= maxHits
                    || AbilityUtils.isInValidEntity(entity,player)
                    || (entityDelayMap.containsKey(entity) && System.currentTimeMillis() < entityDelayMap.get(entity))
            ) continue;

            DamageHandler.damageEntity(entity,player,damage,this);
            entityDelayMap.put(entity,System.currentTimeMillis() + damageDelay);
            entityDamageMap.put(entity,entityDamageMap.getOrDefault(entity,0) + 1);
        }

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
        return "BloodPool";
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

    public class Pool{
        private Location location;
        private long startTime;
        private double currRadius = radius;
        private boolean increment = false;
        public Pool(Location location, long startTime){
            this.location = location;
            this.startTime = startTime;
            makePool();
        }

        public boolean makePool(){
            if (System.currentTimeMillis() - startTime >= 5000){
                return true;
            }else if (GeneralMethods.isRegionProtectedFromBuild(player,location)) {
                return true;
            }

            for (int i = -180; i < 180; i += 30) {
                double angle = i * 3.141592653589793D / 180.0D;
                double x = currRadius * Math.cos(angle + poolRotation);
                double z = currRadius * Math.sin(angle + poolRotation);
                location.add(x, 0, z);
                if (RainbowColor.playParticles(player,location,index)) {
                    location.getWorld().spawnParticle(Particle.BLOCK_DUST, location, 1, 0, 0, 0, Material.REDSTONE_BLOCK.createBlockData());
                }
                location.subtract(x, 0, z);
            }

            currRadius += increment ? .5 : -.5;
            if (currRadius >= radius || currRadius <= 0){
                increment = !increment;
            }

            doDamage(location,currRadius);

            return false;
        }

        public void setLocation(Location location){
            this.location = location.clone();
        }

        public Location getLocation(){
            return location;
        }
    }

    public String getInstructions(){
        return "Sneak at an entity to source from them. Left click again to creat a pool in any direction you're looking.";
    }

    public String getDescription(){
        return "Use this ability to kill your enemies in their own blood. This ability will take your enemies blood and allows you to create pools of it to drown your enemies.";
    }

}
