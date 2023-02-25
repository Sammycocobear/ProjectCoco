package me.scb.Abilities.Fire.Lightning;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.LightningAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager;
import com.projectkorra.projectkorra.util.ClickType;
import de.slikey.effectlib.util.RandomUtils;
import me.scb.Abilities.Water.RainCloud;
import me.scb.Configuration.ConfigManager;
import me.scb.Utils.AbilityUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
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
    private List<ZigZag> thunderStrikes = new ArrayList<>();
    private int ticks;
    public ThunderStorm(Player player) {
        super(player);
        if (CoreAbility.hasAbility(player,getClass()) || !bPlayer.canBendIgnoreBinds(this)) return;
        location = GeneralMethods.getTargetedLocation(player,sourceRange);
        int search = 0;
        while (!location.subtract(0,1,0).getBlock().isSolid() && search++ < 50);
        location.add(0,height + 1,0);

        start();
    }


    public void makeCloud(){
        for (int i = 0; i < 20; i++) {
            Vector v = RandomUtils.getRandomCircleVector().multiply(RandomUtils.random.nextDouble() * radius);
            location.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location.add(v), 1,0,0, 0,0);
            location.subtract(v);
        }
    }

    public void makeThunderVisuals(){
        for (int i = 1; i < (int) (Math.random() * 3); i++){
            Location cloneLocation, floorLocation = location.clone().subtract(0,height + 1,0);
            floorLocation.setPitch(90);
            cloneLocation = location.clone().add(Math.random() * radius - 1,0, Math.random() * radius - 1);
            thunderStrikes.add(new ZigZag(cloneLocation,floorLocation));
        }
    }

    @Override
    public void progress() {
        if (System.currentTimeMillis() - getStartTime() >= 3000){
            remove();
            return;
        }

        if (ticks++ % 20 == 0){
            makeThunderVisuals();
            player.sendMessage(ticks +"");
        }

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
        return null;
    }

    @Override
    public String getVersion() {
        return null;
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
