package me.scb.Abilities.Water.Ice;

import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.IceAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager;
import com.projectkorra.projectkorra.util.ClickType;
import me.scb.Abilities.Water.RainCloud;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Hail extends IceAbility implements AddonAbility, ComboAbility {

    public Hail(Player player) {
        super(player);
        if (CoreAbility.hasAbility(player,getClass()) || !bPlayer.canBendIgnoreBinds(this)) return;
        start();
    }

    //make move forward and create snow for the waterbender to sue

    @Override
    public void progress() {
        List<RainCloud> rainClouds = RainCloud.getRainCloudsInArea(player, ConfigManager.getConfig().getDouble("Abilities.Ice.Hail.SearchRange"));
        for (RainCloud rainCloud : rainClouds){
            if (rainCloud.getPlayer() != player){
                rainCloud.setPlayer(player);
            }
            rainCloud.setHailing(true,this);
        }
        remove();
        return;
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
        return ConfigManager.getConfig().getLong("Abilities.Ice.Hail.Cooldown");
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

    public String getInstructions(){
        return "IceSpike (Tap Sneak) -> FrostBreath (Left Click)";
    }

    public String getDescription(){
        return "With this ability, you lower the tempature near any RainClouds near you. The RainClouds will now rain down ice and snow, damaging anyone under it and giving them slowness.";
    }
}
