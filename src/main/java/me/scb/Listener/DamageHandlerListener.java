package me.scb.Listener;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.util.TempBlock;
import me.scb.ProjectCoco;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DamageHandlerListener implements Listener {


    @EventHandler
    public void onHotFeet(EntityDamageEvent e){
        if (e.getCause() != EntityDamageEvent.DamageCause.HOT_FLOOR) return;
        if (e.getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN).
                hasMetadata("ProjectCoco://LavaAbility://Magma")){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void stopLava(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity player = (LivingEntity) event.getEntity();

        if (event.getCause().equals(EntityDamageEvent.DamageCause.LAVA)){
            boolean lava = false;
            for (Block b : GeneralMethods.getBlocksAroundPoint(player.getLocation(), 2)) {
                if (b.hasMetadata("ProjectCoco://LavaAbility://Lava") && TempBlock.isTempBlock(b)&&b.getType()== Material.LAVA) {
                    lava = true;
                }
            }
            if (lava) {
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        player.setFireTicks(0);
                    }
                }.runTaskLater(ProjectCoco.getPlugin(),1);
                event.setCancelled(true);
            }
        }
    }
}
