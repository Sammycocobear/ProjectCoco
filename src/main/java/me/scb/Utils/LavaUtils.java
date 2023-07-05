package me.scb.Utils;

import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class LavaUtils {
    public static TempBlock createLava(Block block){
        block.setMetadata("ProjectCoco://LavaAbility://Lava",AbilityUtils.getValue());
        return new TempBlock(block, Material.LAVA);
    }

    public static TempBlock createMagma(Block block){
        block.setMetadata("ProjectCoco://LavaAbility://Magma",AbilityUtils.getValue());
        return new TempBlock(block, Material.MAGMA_BLOCK);
    }
}
