package me.scb.Listener;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import com.projectkorra.projectkorra.event.AbilityStartEvent;
import me.scb.Abilities.Air.Sound.BassBoost;
import me.scb.Abilities.Air.Spiritual.SummonSpirits;
import me.scb.Abilities.Chi.ChameleonSuit;
import me.scb.Abilities.Earth.Lava.VolcanicJets;
import me.scb.Abilities.Earth.Lava.Volcano;
import me.scb.Abilities.Earth.Metal.MetalKunais;
import me.scb.Abilities.Earth.Metal.Reconfigure;
import me.scb.Abilities.Earth.Sand.QuickSand;
import me.scb.Abilities.Earth.Sand.SandPad;
import me.scb.Abilities.Earth.Sand.SandTornado;
import me.scb.Abilities.Fire.BlueFire.BlueFireOrbs;
import me.scb.Abilities.Fire.Combustion.CombustionBomb;
import me.scb.Abilities.Fire.Lightning.Railgun;
import me.scb.Abilities.Fire.Lightning.ThunderStorm;
import me.scb.Abilities.Water.Blood.BloodBlink;
import me.scb.Abilities.Water.Blood.BloodPool;
import me.scb.Abilities.Water.Healing.RefreshingRain;
import me.scb.Abilities.Water.Ice.Hail;
import me.scb.Abilities.Water.Ice.IcyGrenade;
import me.scb.Abilities.Water.Plant.SeedSummoner.PeaShooter;
import me.scb.Abilities.Water.Plant.SeedSummoner.SeedSummoner;
import me.scb.Abilities.Water.Plant.TendrilTwist;
import me.scb.Abilities.Water.Plant.ThornyBush;
import me.scb.Abilities.Water.RainCloud;
import me.scb.Utils.FallDamageRemoval;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

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
        } else if (bound.equalsIgnoreCase("Railgun")){
            new Railgun(player);
        } else if (bound.equalsIgnoreCase("IcyGrenade")){
            new IcyGrenade(player);
        } else if (bound.equalsIgnoreCase("BloodPool")){
            new BloodPool(player);
        } else if (bound.equalsIgnoreCase("RefreshingRain")){
            new RefreshingRain(player);
        } else if (bound.equalsIgnoreCase("SummonSpirits")){
            new SummonSpirits(player);
        } else if (bound.equalsIgnoreCase("ThornyBush")){
            new ThornyBush(player);
        } else if (bound.equalsIgnoreCase("TendrilTwist")){
            new TendrilTwist(player);
        } else if (bound.equalsIgnoreCase("VolcanicJets")){
            new VolcanicJets(player);
        } else if (bound.equalsIgnoreCase("BlueFireOrbs")){
            new BlueFireOrbs(player);
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
        } else if (bound.equalsIgnoreCase("Volcano")) {
            new Volcano(player);
        } else if (bound.equalsIgnoreCase("BlueFireOrbs")) {
            BlueFireOrbs blueFireOrbs = CoreAbility.getAbility(player,BlueFireOrbs.class);
            if (blueFireOrbs != null){
                blueFireOrbs.setShoot();
            }
        }else if (bound.equalsIgnoreCase("SandPad")) {
            new SandPad(player);
        } else if (bound.equalsIgnoreCase("BloodPool")) {
            BloodPool bloodPool = CoreAbility.getAbility(player,BloodPool.class);
            if (bloodPool != null){
                bloodPool.selectPool();
            }
        } else if (bound.equalsIgnoreCase("BassBoost")){
            new BassBoost(player);
        } else if (bound.equalsIgnoreCase("TendrilTwist")) {
            final TendrilTwist tendrilTwist = CoreAbility.getAbility(player, TendrilTwist.class);
            if (tendrilTwist != null) {
                tendrilTwist.setShot();
            }

        } else if (bound.equalsIgnoreCase("MetalKunais")){
            new MetalKunais(player);
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

    @EventHandler
    public void onSeedSummonerClick(PlayerInteractEvent e){
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) return;

        final Player player = e.getPlayer();
        final SeedSummoner seedSummoner = CoreAbility.getAbility(player,SeedSummoner.class);
        if (seedSummoner == null) return;
        seedSummoner.activateClickAbilities();

        String bound = seedSummoner.currName;
        if (bound != null && bound.equalsIgnoreCase("PeaShooter")){
            PeaShooter shooter = CoreAbility.getAbility(player,PeaShooter.class);
            if (shooter != null){
                shooter.setShot();
            }
        }
    }

    @EventHandler
    public void onSeedSummonerSneak(PlayerToggleSneakEvent e){
        final Player player = e.getPlayer();
        if (!e.isSneaking()) return;
        final SeedSummoner seedSummoner = CoreAbility.getAbility(player,SeedSummoner.class);
        if (seedSummoner == null) {
            if (!BendingPlayer.getBendingPlayer(player).getBoundAbilityName().equalsIgnoreCase("SeedSummoner")) return;
            new SeedSummoner(player);
            return;
        }
        seedSummoner.activateSneakAbilities();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHopperCollectItem(final InventoryPickupItemEvent event) {
        if (event.getItem().hasMetadata("ProjectCoco://Dropped_Item")) {
            event.getItem().remove();
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMelonGrow(BlockGrowEvent e){
        BlockFace[] faces = {BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
        for (BlockFace face : faces) {
            Block relative = e.getBlock().getRelative(face);
            if (relative.getType() == Material.MELON_STEM && relative.hasMetadata("ProjectCoco://PeaShooter://Melon")) {
                e.setCancelled(true);
                return;
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDamaged(final BlockDamageEvent event) {
        if (event.getInstaBreak())
            return;

        final ItemStack tool = event.getItemInHand();
        if (tool == null || !Reconfigure.TOOL_MATERIALS.contains(tool.getType()))
            return;

        final BendingPlayer bendingPlayer = BendingPlayer.getBendingPlayer(event.getPlayer());
        if (bendingPlayer == null || !bendingPlayer.getBoundAbilityName().equalsIgnoreCase("Reconfigure"))
            return;

        final Material optimalTool = Reconfigure.getOptimalTool(event.getBlock());
        if (optimalTool != null && tool.getType() != optimalTool)
            tool.setType(optimalTool);
    }

    @EventHandler
    public void onPlayerInteraction(final PlayerInteractEvent event) {
        final ItemStack tool = event.getItem();
        if (tool == null || !Reconfigure.TOOL_MATERIALS.contains(tool.getType()))
            return;

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        final Player player = event.getPlayer();
        final BendingPlayer bendingPlayer = BendingPlayer.getBendingPlayer(player);
        if (bendingPlayer == null || !bendingPlayer.getBoundAbilityName().equalsIgnoreCase("Reconfigure"))
            return;

        final ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.IRON_HOE || itemInHand.getType() == Material.IRON_SHOVEL){
            final Block block = event.getClickedBlock();
            if (block != null){
                if (Tag.DIRT.getValues().contains(block.getType())) {
                    return;
                }
            }
        }

        switch (tool.getType()) {
            case IRON_HOE:
                tool.setType(Material.IRON_AXE);
                break;
            case IRON_AXE:
                tool.setType(Material.IRON_SHOVEL);
                break;
            case IRON_SHOVEL:
                tool.setType(Material.IRON_PICKAXE);
                break;
            case IRON_PICKAXE:
                tool.setType(Material.IRON_SWORD);
                break;
            case IRON_SWORD:
                tool.setType(Material.IRON_HOE);
                break;
            case SHEARS:
                tool.setType(Material.SHEARS);
                break;
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
