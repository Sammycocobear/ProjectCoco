package me.scb.Abilities.Earth.Lava;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.LavaAbility;
import com.projectkorra.projectkorra.util.TempBlock;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VolcanicJets extends LavaAbility implements AddonAbility {
    private int jets = ConfigManager.getConfig().getInt("Abilities.Lava.VolcanicJets.Jets");
    private double range = ConfigManager.getConfig().getDouble("Abilities.Lava.VolcanicJets.Range");
    private double jetSpace = range/jets;
    private double distance;
    private double speed = ConfigManager.getConfig().getDouble("Abilities.Lava.VolcanicJets.Speed");
    private Location location;
    private double height = ConfigManager.getConfig().getDouble("Abilities.Lava.VolcanicJets.Jet.Height");
    private long delay = ConfigManager.getConfig().getLong("Abilities.Lava.VolcanicJets.Jet.SpawnDelay");

    private List<Jet> jetList = new ArrayList<>();
    public VolcanicJets(Player player) {
        super(player);
        if (CoreAbility.hasAbility(player,getClass()) || !bPlayer.canBend(this)) return;

        location = player.getLocation();
        location.setPitch(0);
        start();
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void progress() {

        if (player.isDead() || !player.isOnline()){
            remove();
            return;
        } else if (GeneralMethods.isRegionProtectedFromBuild(this,location)) {
            remove();
            return;
        }

        if (jetList.isEmpty() && distance >= range){
            remove();
            return;
        }


        if (distance <= range) {
            location.add(location.getDirection().multiply(speed));
            if ((int) distance % jetSpace  == 0){
                spawnJet();
            }
            location.getWorld().spawnParticle(Particle.LAVA,location,5,.25,.25,.25,.2);
        }

        distance += speed;
        jetList.removeIf(Jet::run);


    }

    public void spawnJet() {
        jetList.add(new Jet(location.clone()));
    }


    @Override
    public void remove() {
        super.remove();
        player.sendMessage("removed");
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
        return ConfigManager.getConfig().getLong("Abilities.Lava.VolcanicJets.Cooldown");
    }

    @Override
    public String getName() {
        return "VolcanicJets";
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

    public class Jet{
        private Location location;
        private double y = 0;
        private long curr = System.currentTimeMillis() + delay; //todo delay
        public Jet(Location location){
            this.location = location;
            y = location.getY();

        }

        public List<TempBlock> tempBlocks = new ArrayList<>();

        public boolean run(){
            if (location.getY() - y >= height){
                tempBlocks.forEach(TempBlock::revertBlock);
                return true;
            }
            if (System.currentTimeMillis() >= curr) {
                curr = System.currentTimeMillis() + 500;
                for (Block block : GeneralMethods.getBlocksAroundPoint(location, 1)) {
                    if (!block.getType().isAir()) continue;
                    tempBlocks.add(new TempBlock(block, Material.LAVA.createBlockData()));
                }
                location.add(0,1,0);
            }

            return false;
        }
    }
}
