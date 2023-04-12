package me.scb.Abilities.Fire.Lightning;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.LightningAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.DamageHandler;
import de.slikey.effectlib.util.RandomUtils;
import me.scb.Abilities.Water.RainCloud;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;


public class ThunderStorm extends LightningAbility implements AddonAbility, ComboAbility {
    private final double radius = ConfigManager.getConfig().getDouble("Abilities.Lightning.ThunderStorm.Radius");
    private final double sourceRange = ConfigManager.getConfig().getDouble("Abilities.Lightning.ThunderStorm.SourceRange");
    private final long cooldown = ConfigManager.getConfig().getLong("Abilities.Lightning.ThunderStorm.Cooldown");
    private final double height = ConfigManager.getConfig().getLong("Abilities.Lightning.ThunderStorm.Height");


    private Location location;
    private final List<ZigZag> thunderStrikes = new ArrayList<>();
    private int ticks;
    public ThunderStorm(Player player) {
        super(player);
        if (CoreAbility.hasAbility(player,getClass()) || !bPlayer.canBendIgnoreBinds(this)) return;
        location = GeneralMethods.getTargetedLocation(player,sourceRange);
        int search = 0;
        while (!location.subtract(0,1,0).getBlock().isSolid() && search++ < 50);
        location.add(0,height + 1,0);
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE,1.5f,.6f);

        start();
    }


    public void makeCloud(){
        for (int i = 0; i < 20; i++) {
            Vector v = RandomUtils.getRandomCircleVector().multiply(RandomUtils.random.nextDouble() * radius);
            location.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location.add(v), 1,0,0, 0,0);
            location.subtract(v);
        }
    }


    public void doDamage(Location location) {
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, 1)) {
            if (AbilityUtils.isInValidEntity(entity, player) /*|| entityList.contains(entity)*/) continue;
            DamageHandler.damageEntity(entity,player,2,this);
        }

    }

    public void makeThunderVisuals(){
        location.getWorld().spawnParticle(Particle.FLASH,location,1,.5,.5,.5,0);
        for (int i = 0; i < (int) (Math.random() * 5); i++){
            Location cloneLocation, floorLocation;
            cloneLocation = location.clone().add(RandomUtils.getRandomCircleVector().multiply(RandomUtils.random.nextDouble() * radius - 1));
            floorLocation = cloneLocation.clone().subtract(0,height + 1, 0);
            floorLocation.setPitch(90);
            thunderStrikes.add(new ZigZag(cloneLocation,floorLocation, player));
        }
    }

    @Override
    public void progress() {
        if (System.currentTimeMillis() - getStartTime() >= 3000){
            remove();
            return;
        }

        if (ticks++ % 4 == 0){
            makeThunderVisuals();
        }
        for (int i = 0; i < thunderStrikes.size(); i++) {
            ZigZag zigZag = thunderStrikes.get(i);
            if (zigZag.zigZag()){
                thunderStrikes.remove(i--);
            }
            doDamage(zigZag.getLocation());
        }
        thunderStrikes.removeIf(ZigZag::zigZag);
        makeCloud();
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
        return "ThunderStorm";
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
        return ProjectCoco.getAuthor();
    }

    @Override
    public String getVersion() {
        return ProjectCoco.getVersion();
    }


    @Override
    public Object createNewComboInstance(Player player) {
        return new ThunderStorm(player);
    }

    @Override
    public ArrayList<ComboManager.AbilityInformation> getCombination() {
        ArrayList<ComboManager.AbilityInformation> re = new ArrayList<>();
        re.add(new ComboManager.AbilityInformation("Lightning", ClickType.LEFT_CLICK));
        return re;
    }


}
