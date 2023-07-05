package me.scb.Abilities.Water.Ice;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.ElementalAbility;
import com.projectkorra.projectkorra.ability.IceAbility;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.TempBlock;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

public class IcyGrenade extends IceAbility implements AddonAbility {
    private final double selectRange = ConfigManager.getConfig().getDouble("Abilities.Ice.IcyGrenade.SourceRange");

    private boolean hasShot = false;
    Block sourceBlock = null;
    FallingBlock fallingBlock = null;
    public IcyGrenade(Player player) {
        super(player);
        final IcyGrenade c = CoreAbility.getAbility(player,getClass());
        if (c != null && !c.hasShot) {
            c.remove();
        }
        if (bPlayer.isOnCooldown(this) || ! bPlayer.canBend(this)) return;
        sourceBlock = BlockSource.getWaterSourceBlock(player, selectRange, ClickType.SHIFT_DOWN, bPlayer.canPlantbend(), bPlayer.canIcebend(), bPlayer.canPlantbend());
        if (sourceBlock == null) return;
        start();
    }

    public void setShot(){
        if (hasShot)return;
        hasShot = true;
        fallingBlock = AbilityUtils.createFallingBlock(sourceBlock.getLocation().add(.5,1,.5), Material.ICE);
        Location l = player.getLocation();
        fallingBlock.setVelocity(l.getDirection().multiply(1.5));
        bPlayer.addCooldown(this,3000);
    }
    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void progress() {
        if (!hasShot){
            playFocusWaterEffect(sourceBlock);
        }else{
            fallingBlock.getWorld().spawnParticle(Particle.BLOCK_DUST,fallingBlock.getLocation(),1,.5,.5,.5,.1,Material.ICE.createBlockData());
            fallingBlock.getWorld().spawnParticle(Particle.FIREWORKS_SPARK,fallingBlock.getLocation(),1,.5,.5,.5,.1);
            fallingBlock.getWorld().spawnParticle(Particle.END_ROD,fallingBlock.getLocation(),1,.5,.5,.5,.05);

            if (fallingBlock.getLocation().subtract(0,.3,0).getBlock().isSolid() || fallingBlock.isDead()){
                for (Block b : GeneralMethods.getBlocksAroundPoint(fallingBlock.getLocation(),5)) {
                    if (!ElementalAbility.isEarth(b)) continue;
                    new TempBlock(b,Material.ICE.createBlockData(),3000);
                }
                remove();
                return;
            }
        }

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
        return "IcyGrenade";
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
