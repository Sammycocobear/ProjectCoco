package me.scb.Listener;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import me.scb.Abilities.Earth.Lava.Erupt;
import me.scb.Abilities.Earth.Lava.LavaBall;
import me.scb.Abilities.Earth.Sand.QuickSand;
import me.scb.Abilities.Earth.Sand.SandTornado;
import me.scb.Abilities.Fire.Combustion.CombustionBomb;
import me.scb.Abilities.Fire.Lightning.Railgun;
import me.scb.Abilities.Fire.Lightning.ThunderStorm;
import me.scb.Abilities.Water.Blood.BloodBlink;
import me.scb.Abilities.Water.Blood.BloodRush;
import me.scb.Abilities.Water.Ice.Hail;
import me.scb.Abilities.Water.Ice.IcyGrenade;
import me.scb.Abilities.Water.RainCloud;
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
        }else if (bound.equalsIgnoreCase("LavaBall")){
            new LavaBall(player);
        }else if (bound.equalsIgnoreCase("Railgun")){
            new Railgun(player);
        }else if (bound.equalsIgnoreCase("BloodRush")){
            new BloodRush(player);
        }else if (bound.equalsIgnoreCase("IcyGrenade")){
            new IcyGrenade(player);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) return;
        final Player player = e.getPlayer();
        final BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        if (bPlayer == null) return;
        String bound = bPlayer.getBoundAbilityName();
        if (bound.equalsIgnoreCase("SandTornado")) {
            final SandTornado torn = CoreAbility.getAbility(player, SandTornado.class);
            if (torn != null) {
                torn.setVariables(false);
            }
        } else if (bound.equalsIgnoreCase("QuickSand")) {
            new QuickSand(player);
        } else if (bound.equalsIgnoreCase("CombustionBomb")) {
            new CombustionBomb(player);
        } else if (bound.equalsIgnoreCase("Erupt")) {
            new Erupt(player);
        } else if (bound.equalsIgnoreCase("IcyGrenade")) {
            final IcyGrenade icyGrenade = CoreAbility.getAbility(player, IcyGrenade.class);
            if (icyGrenade != null) {
                icyGrenade.setShot();
            }

        } else if (bound.equalsIgnoreCase("BloodBlink")) {
            final BloodBlink bloodBlink = CoreAbility.getAbility(player, BloodBlink.class);
            if (bloodBlink != null) {
                bloodBlink.jump();
            }else{
                new BloodBlink(player);
            }
        } else if (bound.equalsIgnoreCase("RainCloud")) {
            new RainCloud(player);
        } else if (bound.equalsIgnoreCase("Hail")) {
            new Hail(player);
        } else if (bound.equalsIgnoreCase("ThunderStorm")) {
            new ThunderStorm(player);
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
