package me.scb.Abilities.Earth.Lava;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.LavaAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.TempBlock;
import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import me.scb.Utils.FallDamageRemoval;
import me.scb.Utils.LavaUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Erupt extends LavaAbility implements AddonAbility {
    private final long timeBeforeEruption = 1000;
    private final double eruptionHeight = 15;
    private final double eruptionSpeed = .75;
    private final double knockup = 1.5;
    private double height = 0;
    private final double damage = 2;
    private final List<Entity> entityList = new ArrayList<>();
    private final List<TempBlock> tempBlockList = new ArrayList<>();
    private final Location sourceLocation;
    private final double sourceRange = 10;
    private final double radius = 2;
    public Erupt(Player player) {
        super(player);
        sourceLocation = GeneralMethods.getTargetedLocation(player,sourceRange);
        int searches = 0;
        while (searches++ < 20 && !sourceLocation.subtract(0,1,0).getBlock().isSolid());
        for (Block b : GeneralMethods.getBlocksAroundPoint(sourceLocation,radius)){
            if (!isEarthbendable(b)) continue;
            tempBlockList.add(LavaUtils.createMagma(b));
        }

        start();
    }

    public void erupt(){
        for (Block b : GeneralMethods.getBlocksAroundPoint(sourceLocation.clone().add(0,height,0),radius - 1)){
            if (!isEarthbendable(b) && !isAir(b.getType()))continue;
            tempBlockList.add(LavaUtils.createLava(b));
        }
        doDamage(sourceLocation);
        if (height < eruptionHeight){
            height += eruptionSpeed;
        }else{
            remove();
            return;
        }
    }

    public void doDamage(Location location){
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location,radius)){
            if (AbilityUtils.isInValidEntity(entity,player) || entityList.contains(entity)) continue;
            DamageHandler.damageEntity(entity,player,damage,this);
            GeneralMethods.setVelocity(this,entity,new Vector(0,knockup,0));
            entityList.add(entity);
            FallDamageRemoval.addFallDamageCap((LivingEntity) entity,0);
        }
    }

    @Override
    public void progress() {
        if (System.currentTimeMillis() - getStartTime() >= timeBeforeEruption){
            erupt();
        }

    }

    @Override
    public void remove() {
        super.remove();
        tempBlockList.forEach(TempBlock::revertBlock);
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
        return "Erupt";
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
