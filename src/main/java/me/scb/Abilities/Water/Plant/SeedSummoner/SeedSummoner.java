package me.scb.Abilities.Water.Plant.SeedSummoner;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.MultiAbility;
import com.projectkorra.projectkorra.ability.PlantAbility;
import com.projectkorra.projectkorra.ability.util.MultiAbilityManager;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SeedSummoner extends PlantAbility implements AddonAbility, MultiAbility {
    List<CoreAbility> abilities = new ArrayList<>();
    private final int maxBarrierBeans = ConfigManager.getConfig().getInt("Abilities.Plant.SeedSummoner.MaxBarrierBeans");
    private final int maxPeaShoots = ConfigManager.getConfig().getInt("Abilities.Plant.SeedSummoner.MaxPeaShots");
    private final int maxThornBarrages = ConfigManager.getConfig().getInt("Abilities.Plant.SeedSummoner.MaxThornBarrages");
    private final int maxChiliBeans = ConfigManager.getConfig().getInt("Abilities.Plant.SeedSummoner.MaxChiliBeans");

    private boolean remove = false;
    public String currName = SeedState.BarrierBean.toString();

    @Override
    public boolean isEnabled() {
        return false;
    }

    public enum SeedState {
        BarrierBean(10),
        ThornBarrage(20),
        ChiliBean(15),
        PeaShooter(25),
        EXIT(100);

        private int maximum;

        SeedState(int maximum) {
            this.maximum = maximum;
        }

        public int getMaximum() {
            return maximum;
        }

        public void setMaximum(int newMax) {
            maximum =  newMax;
        }
    }

    private SeedState currentState = SeedState.BarrierBean;

    public SeedSummoner(Player player) {
        super(player);
        if (CoreAbility.hasAbility(player,getClass()) || !bPlayer.canBend(this)) return;
        MultiAbilityManager.bindMultiAbility(this.player, "SeedSummoner");
        SeedState.BarrierBean.setMaximum(maxBarrierBeans);
        SeedState.ThornBarrage.setMaximum(maxThornBarrages);
        SeedState.ChiliBean.setMaximum(maxChiliBeans);
        SeedState.PeaShooter.setMaximum(maxPeaShoots);

        start();
    }

    @Override
    public void progress() {
        if (!this.player.isOnline() || this.player.isDead()) {
            this.remove();
            return;
        }
        if (remove){
            remove();
            return;
        }
        final int slot = player.getInventory().getHeldItemSlot();
        currentState = SeedState.values()[slot];
        currName = currentState.toString();

    }

    //this will work i think as long as i ensure the classes names are the same as the enums
    public int getCountOfCurrentState(){
        int count = 0;
        for (CoreAbility ability : abilities) {
            if (ability.getName().equals(currentState.toString())){
                count++;
            }
        }
        return count;
    }


    public void sendAbilityUseMessage(){
        int curr = getCountOfCurrentState();
        int amount = currentState.getMaximum() - curr;
        String message = amount > 0 ?
                ChatColor.GREEN + "Usages Left: " + (amount) :
                ChatColor.RED + "NO MORE USAGES";

        player.sendActionBar(message);
    }

    public boolean cantStartAbility(){
        return currentState.getMaximum() - getCountOfCurrentState() <= 0;
    }

    public void activateClickAbilities(){
        if (cantStartAbility()){
            return;
        }
        switch (currentState){
            case BarrierBean:
                BarrierBean bean = new BarrierBean(player);
                if (bean.isStarted()) {
                    abilities.add(bean);
                }
                break;
            case ChiliBean:
                ChiliBean cBean = new ChiliBean(player);
                if (cBean.isStarted()) {
                    abilities.add(cBean);
                }
                break;
            case EXIT:
                remove = true;
                return;
        }
        sendAbilityUseMessage();

    }


    public void activateSneakAbilities() {
        if (cantStartAbility()) {
            return;
        }
        switch (currentState) {
            case ThornBarrage:
                ThornBarrage thorn = new ThornBarrage(player);
                if (thorn.isStarted()){
                    abilities.add(thorn);
                }
                break;
            case PeaShooter:
                PeaShooter peaShooter = new PeaShooter(player);
                if (peaShooter.isStarted()) {
                    abilities.add(peaShooter);
                }
                break;
        }
        sendAbilityUseMessage();
    }

    public void removeAbility(CoreAbility thi){
        abilities.remove(thi);
    }

    @Override
    public void remove() {
        super.remove();
        MultiAbilityManager.unbindMultiAbility(this.player);

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
        return "SeedSummoner";
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

    public String getVersion() {
        return ProjectCoco.getVersion();
    }


    @Override
    public ArrayList<MultiAbilityManager.MultiAbilityInfoSub> getMultiAbilities() {
        ArrayList<MultiAbilityManager.MultiAbilityInfoSub> multiAbilityInfoSubs = new ArrayList<>();
        multiAbilityInfoSubs.add(new MultiAbilityManager.MultiAbilityInfoSub("BarrierBean", Element.PLANT));
        multiAbilityInfoSubs.add(new MultiAbilityManager.MultiAbilityInfoSub("ThornBarrage", Element.PLANT));
        multiAbilityInfoSubs.add(new MultiAbilityManager.MultiAbilityInfoSub("ChiliBean", Element.PLANT));
        multiAbilityInfoSubs.add(new MultiAbilityManager.MultiAbilityInfoSub("Peashooter", Element.PLANT));

        multiAbilityInfoSubs.add(new MultiAbilityManager.MultiAbilityInfoSub("EXIT", Element.FIRE));

        return multiAbilityInfoSubs;
    }
}
