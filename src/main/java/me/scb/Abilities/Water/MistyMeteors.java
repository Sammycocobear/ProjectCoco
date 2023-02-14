package me.scb.Abilities.Water;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.WaterAbility;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MistyMeteors extends WaterAbility implements AddonAbility {
    private final int maxSources = ConfigManager.getConfig().getInt(AbilityUtils.getConfigPatch(this,"MaxSources"));
    private final double sourceRange = ConfigManager.getConfig().getInt(AbilityUtils.getConfigPatch(this,"SourceRange"));
    private final double sourceRadius = ConfigManager.getConfig().getInt(AbilityUtils.getConfigPatch(this,"SourceRadius"));
    private List<Block> nearby;
    public MistyMeteors(Player player) {
        super(player);
        if (CoreAbility.hasAbility(player,getClass()) || !bPlayer.canBend(this)) return;
        final Location target = GeneralMethods.getTargetedLocation(player,sourceRange);
        nearby = getWaterBendableNear(target,sourceRadius);
        if (nearby.size() < maxSources) return;

        //start();
    }

    public List<Block> getWaterBendableNear(Location location, double radius){
        List<Block> returnList = new ArrayList<>();
        for (Block block : GeneralMethods.getBlocksAroundPoint(location,radius)){
            if (isWaterbendable(block)){
                returnList.add(block);
            }
        }
        return returnList;
    }

    @Override
    public void progress() {
        remove();
        return;
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
        return "MistyMeteors";
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
