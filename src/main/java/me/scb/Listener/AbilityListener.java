package me.scb.Listener;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import com.projectkorra.projectkorra.event.AbilityStartEvent;
import me.scb.Abilities.Chi.ChameleonSuit;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
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
        } else if (bound.equalsIgnoreCase("ChameleonSuit")) {
            final ChameleonSuit suit = CoreAbility.getAbility(player, ChameleonSuit.class);
            if (suit != null)
                suit.remove();
            else
                new ChameleonSuit(player);
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onTarget(final EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player))
            return;

        final Player player = (Player) event.getTarget();
        final ChameleonSuit suit = CoreAbility.getAbility(player, ChameleonSuit.class);
        if (suit != null && !suit.canSeePlayer(event.getEntity()))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAbilityStart(final AbilityStartEvent event) {
        final Ability ability = event.getAbility();
        if (ability instanceof PassiveAbility)
            return;

        final ChameleonSuit suit = CoreAbility.getAbility(ability.getPlayer(), ChameleonSuit.class);
        if (suit != null)
            suit.remove();
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamageDealt(final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player))
            return;

        final ChameleonSuit suit = CoreAbility.getAbility((Player) event.getDamager(), ChameleonSuit.class);
        if (suit != null)
            suit.remove();
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        final Player player = (Player) event.getEntity();
        final ChameleonSuit suit = CoreAbility.getAbility(player, ChameleonSuit.class);
        if (suit != null && suit.shouldDisengageOnHit())
            suit.remove();
    }
}
