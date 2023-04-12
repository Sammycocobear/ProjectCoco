package me.scb.Abilities.Water.Ice;

import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.IceAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager;
import com.projectkorra.projectkorra.util.ClickType;
import me.scb.Abilities.Water.RainCloud;
import me.scb.ProjectCoco;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Hail extends IceAbility implements AddonAbility, ComboAbility {
    public Hail(Player player) {
        super(player);
        List<RainCloud> rainClouds = RainCloud.getRainCloudsInArea(player,50);
        for (RainCloud rainCloud : rainClouds){
            if (rainCloud.getPlayer() != player){
                rainCloud.setPlayer(player);
            }
            rainCloud.setHailing(true,this);
        }
    }

    @Override
    public void progress() {

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
        return "Hail";
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

    @Override
    public Object createNewComboInstance(Player player) {
        return new Hail(player);
    }

    @Override
    public ArrayList<ComboManager.AbilityInformation> getCombination() {
        ArrayList<ComboManager.AbilityInformation> returnList = new ArrayList<>();
        returnList.add(new ComboManager.AbilityInformation("IceSpike", ClickType.SHIFT_DOWN));
        returnList.add(new ComboManager.AbilityInformation("IceSpike", ClickType.SHIFT_UP));
        returnList.add(new ComboManager.AbilityInformation("FrostBreath", ClickType.LEFT_CLICK));
        return returnList;
    }
}
