package me.scb.Abilities.Air.Spiritual;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.SpiritualAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import me.scb.Utils.FallDamageRemoval;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SummonSpirits extends SpiritualAbility implements AddonAbility {
    private Map<Vex,Vector> vexes = new HashMap<>();
    private int maxVexes = Math.max(6,2);
    private double speed = 5/100.0;
    private double range = 15;
    private Location endLocation,origin;
    private double spaceBetween = 2;
    private List<Entity> entityList = new ArrayList<>();

    public void createVexes(){
        MetadataValue value = new FixedMetadataValue(ProjectCoco.getPlugin(),1);
        for (int i = -maxVexes/2; i < maxVexes/2 + 1; i++) {
            if (i == 0){
                continue;
            }

            final Location spawnLocation = i > 0 ?
                    GeneralMethods.getLeftSide(player.getEyeLocation(),spaceBetween * i) :
                    GeneralMethods.getRightSide(player.getEyeLocation(),spaceBetween * -i);
            Vex vex = (Vex) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.VEX);
            vex.setCharging(true);
            vex.setInvulnerable(true);
            vex.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
            Bukkit.getMobGoals().removeAllGoals(vex);
            vex.getPathfinder().moveTo(vex.getLocation().add(GeneralMethods.getDirection(vex.getLocation(),endLocation)));
            vex.clearLootTable();
            vex.setMetadata("ProjectCoco://SummonSpirit://Vex",value);
            vex.setCustomName(ChatColor.of("#967bb6") + "Spirit");
            vexes.put(vex,GeneralMethods.getDirection(spawnLocation,endLocation).multiply(speed));
        }
    }


    public SummonSpirits(Player player) {
        super(player);
        endLocation = GeneralMethods.getTargetedLocation(player,range);
        origin = player.getEyeLocation();
        createVexes();
        start();
    }

    public void doDamage(Location location) {
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, 1)) {
            if (AbilityUtils.isInValidEntity(entity, player) || entityList.contains(entity)) continue;
            DamageHandler.damageEntity(entity,player,2,this);
            entityList.add(entity);
        }

    }


    @Override
    public void progress() {
        for (Vex vex : vexes.keySet()){
            if (vex.getLocation().distanceSquared(origin) >= (range * range)){
                remove();
                return;
            }
            vex.setVelocity(vexes.get(vex));
            vex.lookAt(endLocation);
            doDamage(vex.getLocation());
        }

    }

    @Override
    public void remove() {
        super.remove();
        vexes.forEach((v,k) -> v.remove());
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
        return "SummonSpirits";
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
