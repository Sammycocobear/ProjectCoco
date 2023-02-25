package me.scb.Utils;

import com.projectkorra.projectkorra.util.TempBlock;
import me.scb.ProjectCoco;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

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
