package me.scb.Abilities.Earth.Metal;

import com.google.common.collect.ImmutableSet;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.MetalAbility;
import me.scb.ProjectCoco;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Comparator;

public class Reconfigure extends MetalAbility implements AddonAbility {

    public static final ImmutableSet<Material> TOOL_MATERIALS = ImmutableSet.of(
            Material.IRON_HOE,
            Material.IRON_AXE,
            Material.IRON_SHOVEL,
            Material.IRON_PICKAXE,
            Material.IRON_SWORD,
            Material.SHEARS
    );
    private static final ImmutableSet<ItemStack> TOOL_ITEMS = ImmutableSet.of(
            new ItemStack(Material.IRON_HOE, 1),
            new ItemStack(Material.IRON_AXE, 1),
            new ItemStack(Material.IRON_SHOVEL, 1),
            new ItemStack(Material.IRON_PICKAXE, 1),
            new ItemStack(Material.IRON_SWORD, 1)

    );

    @Nullable
    public static Material getOptimalTool(final Block block) {
        if (Tag.WOOL.isTagged(block.getType()) || Tag.WOOL_CARPETS.isTagged(block.getType())) {
            return Material.SHEARS;
        }
        return TOOL_ITEMS.stream()
                .filter(tool -> !block.getDrops(tool).isEmpty())
                .max(Comparator.comparingDouble(block::getDestroySpeed))
                .map(ItemStack::getType)
                .orElse(null);
    }

    public Reconfigure(final Player player) {
        super(player);
    }

    @Override
    public void progress() { }

    @Override
    public boolean isSneakAbility() {
        return false;
    }

    @Override
    public boolean isHarmlessAbility() {
        return true;
    }

    @Override
    public long getCooldown() {
        return 0;
    }

    @Override
    public String getName() {
        return "Reconfigure";
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
        super.remove();
    }

    @Override
    public String getAuthor() {
        return ProjectCoco.getAuthor();
    }

    @Override
    public String getVersion() {
        return ProjectCoco.getVersion();
    }

    public String getInstructions(){
        return "Hold an iron tool on the Reconfigure slot, start breaking a block for it to morph into the best block for that, or right click to cycle through the tool options.";
    }

    public String getDescription(){
        return "This metal-based ability requires an iron tool. You can transform your iron tool into any other iron tool. Additionally, when attempting to break a block, the tool will automatically morph into the most suitable option for that specific block. Alternatively, right-clicking will allow you to cycle through the available tool options.";
    }
}