package me.scb.Listener;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import me.scb.Abilities.Earth.Sand.QuickSand;
import me.scb.Abilities.Earth.Sand.SandTornado;
import me.scb.Abilities.Fire.Combustion.CombustionBomb;
import me.scb.Utils.FallDamageRemoval;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class AbilityListener implements Listener {
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e){
        if (!e.isSneaking()) return;
        final Player player = e.getPlayer();
        final BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        if (bPlayer == null) return;
        String bound = bPlayer.getBoundAbilityName();
        if (bound.equalsIgnoreCase("SandTornado")){
            final SandTornado torn = CoreAbility.getAbility(player,SandTornado.class);

            if (torn != null){
                torn.setVariables(true);
            }else{
                new SandTornado(player);
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e){
        if ( e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) return;
        final Player player = e.getPlayer();
        final BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        if (bPlayer == null) return;
        String bound = bPlayer.getBoundAbilityName();
        if (bound.equalsIgnoreCase("SandTornado")){
            final SandTornado torn = CoreAbility.getAbility(player,SandTornado.class);
            if (torn != null){
                torn.setVariables(false);
            }
        }else if (bound.equalsIgnoreCase("QuickSand")){
            new QuickSand(player);
        }else if (bound.equalsIgnoreCase("CombustionBomb")){
            new CombustionBomb(player);
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onFall(EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && entity instanceof LivingEntity) {
            final LivingEntity livingEntity = (LivingEntity) entity;
            if (FallDamageRemoval.hasFallDamageCap(livingEntity)) {
                final double damageCap = FallDamageRemoval.getFallDamageCap(livingEntity);
                if (damageCap <= 0) {
                    event.setCancelled(true);
                } else {
                    event.setDamage(Math.min(damageCap, event.getDamage()));
                }
                FallDamageRemoval.removeFallDamageCap(livingEntity);
            }
        }
    }


    @EventHandler
    public void onFallingBlock(EntityChangeBlockEvent e){
        if (e.getEntity().hasMetadata("ProjectCoco://TempFallingBlock")){
            e.setCancelled(true);
        }
    }
}
