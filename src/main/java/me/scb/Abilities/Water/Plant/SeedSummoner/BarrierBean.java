package me.scb.Abilities.Water.Plant.SeedSummoner;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.PlantAbility;
import com.projectkorra.projectkorra.ability.util.MultiAbilityManager;
import com.projectkorra.projectkorra.util.TempArmor;
import com.projectkorra.projectkorra.util.TempBlock;
import me.scb.Utils.AbilityUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class BarrierBean extends PlantAbility {
    private long duration = 5000;
    private Location location;
    private long delay = 250;
    private long next = 0;
    private int height = 1,maxHeight = 5;
    private int radius = 3;

    public BarrierBean(Player player) {
        super(player);
        if (!bPlayer.canBendIgnoreBinds(this)) return;

        location = GeneralMethods.getTargetedLocation(player,5).add(0,1,0);
        int search = 0;
        while (!location.subtract(0,1,0).getBlock().isSolid() && search++ < 50);
        location = location.getBlock().getRelative(BlockFace.UP,2).getLocation().add(0,1,0);
        new TempArmor(player,new ItemStack[]{new ItemStack(Material.DIAMOND_HELMET),null,null,null}); //idk if this is correct syntax        start();
    }

    public void createLayer(){
        long time = duration - (System.currentTimeMillis() -  getStartTime()); //get time left in move this isn't necesary and i can make a list and remove it in remove() but wtv

        for (Location location : GeneralMethods.getCircle(location,radius,1,false,true,0)){
            Block b = location.getBlock();
            if (b.isSolid()) continue;
            new TempBlock(b, Material.STRIPPED_JUNGLE_WOOD.createBlockData(),time);
        }


        location.add(0,1,0);
    }

    @Override
    public void progress() {
        if (System.currentTimeMillis() - getStartTime() >= duration){
            remove();
            return;
        }

        if (System.currentTimeMillis() >= next){
            next = System.currentTimeMillis() + delay;
            if (maxHeight > height++) {
                createLayer();
            }
        }
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
        return 0;
    }

    @Override
    public String getName() {
        return "BarrierBean";
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public boolean isHiddenAbility() {
        return true;
    }
}
