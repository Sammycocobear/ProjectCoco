package me.scb.Utils;

import me.scb.ProjectCoco;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class TempFallingBlock {
    private FallingBlock fallingBlock;
    private final static MetadataValue metaDataValue = new FixedMetadataValue(ProjectCoco.getPlugin(),true);

    public TempFallingBlock(Location spawnLocation, BlockData data){
        this.fallingBlock = spawnLocation.getWorld().spawnFallingBlock(spawnLocation,data);
        fallingBlock.setDropItem(false);
        fallingBlock.setMetadata("ProjectCoco://TempFallingBlock",metaDataValue);
    }

    public FallingBlock getFallingBlock(){
        return fallingBlock;
    }
}
