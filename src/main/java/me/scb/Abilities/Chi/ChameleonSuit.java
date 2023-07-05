package me.scb.Abilities.Chi;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ChiAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import me.scb.Configuration.ConfigManager;
import me.scb.Utils.AbilityUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ChameleonSuit extends ChiAbility implements AddonAbility {
	private final static Component COMPONENT_HIDDEN = Component.text("HIDDEN")
			.color(NamedTextColor.DARK_PURPLE)
			.decorate(TextDecoration.BOLD);
	private final static Component COMPONENT_DETECTED = Component.text("DETECTED!")
			.color(NamedTextColor.DARK_RED)
			.decorate(TextDecoration.BOLD);

	private final static Map<EquipmentSlot, ItemStack> NO_EQUIPMENT = new HashMap<>();
	private final static PotionEffect INVISIBILITY
			= new PotionEffect(PotionEffectType.INVISIBILITY, 2, 0, false, false, false);

	static {
		for (final EquipmentSlot slot : EquipmentSlot.values())
			NO_EQUIPMENT.put(slot, new ItemStack(Material.AIR));
	}

	private long cooldown;
	private World origin;

	private double detectionDistance;
	private boolean disengageOnHit;

	private PotionEffect speed;
	private BossBar status;

	public ChameleonSuit(final Player player) {
		super(player);

		if (CoreAbility.hasAbility(this.player, ChameleonSuit.class) || !this.bPlayer.canBend(this))
			return;

		final FileConfiguration config = ConfigManager.getConfig();
		this.cooldown = config.getLong(AbilityUtils.getConfigPatch(this, "Cooldown"));
		this.detectionDistance = config.getDouble(AbilityUtils.getConfigPatch(this, "DetectionDistance"));
		this.disengageOnHit = config.getBoolean(AbilityUtils.getConfigPatch(this, "DisengageOnHit"));

		this.speed = new PotionEffect(PotionEffectType.SPEED, 2, config.getInt(AbilityUtils.getConfigPatch(this, "SpeedPotency")), false, false, false);
		this.status = BossBar.bossBar(Component.text(), 1.0f, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS, Set.of(BossBar.Flag.DARKEN_SCREEN));

		this.origin = this.player.getWorld();

		if (this.bPlayer.isIlluminating())
			this.bPlayer.toggleIllumination();
		if (this.bPlayer.isTremorSensing())
			this.bPlayer.toggleTremorSense();

		for (final CoreAbility ability : CoreAbility.getAbilitiesByInstances()) {
			if (!(ability instanceof PassiveAbility) && this.player.equals(ability.getPlayer()))
				ability.remove();
		}

		GeneralMethods.getEntitiesAroundPoint(this.player.getLocation(), 32.0).stream()
				.filter(e -> e instanceof Mob && this.player.equals(((Mob) e).getTarget()) && !this.canSeePlayer(e))
				.forEach(e -> ((Mob) e).setTarget(null));

		Audience.audience(this.player).showBossBar(status);
		this.origin.playSound(this.player.getLocation(), Sound.BLOCK_BELL_USE, 1.0f, 0.35f);

		this.start();
	}

	public boolean shouldDisengageOnHit() {
		return this.disengageOnHit;
	}

	public boolean canSeePlayer(final Entity entity) {
		final Location entityLoc = entity.getLocation().add(0.0, entity.getHeight(), 0.0); // ghetto #getEyeLocation()
		final Location playerLoc = this.player.getEyeLocation();
		return !GeneralMethods.isObstructed(entityLoc, playerLoc) && entityLoc.distance(playerLoc) <= this.detectionDistance;
	}

	@Override
	public void progress() {
		if (!this.player.isOnline() || this.player.isDead() || !this.player.getWorld().equals(this.origin)) {
			this.remove();
			return;
		}

		this.updateAll();

		final List<Entity> detectors = GeneralMethods.getEntitiesAroundPoint(this.player.getLocation(), this.detectionDistance).stream()
				.filter(e -> e instanceof LivingEntity && !e.equals(this.player))
				.filter(this::canSeePlayer)
				.collect(Collectors.toList());

		if (!detectors.isEmpty()) {
			this.status.name(COMPONENT_DETECTED);
			this.status.color(BossBar.Color.RED);
			this.setWarningDistance(Integer.MAX_VALUE);

			if (this.getRunningTicks()%2 == 0) {
				final Location loc = this.player.getLocation();
				for (final Entity detector : detectors) {
					if (!(detector instanceof Player))
						continue;

					final Player p = (Player) detector;
					p.spawnParticle(Particle.SHRIEK, loc, 1, 0);
				}
			}
		} else {
			this.status.name(COMPONENT_HIDDEN);
			this.status.color(BossBar.Color.PURPLE);
			this.setWarningDistance(0);
		}

		INVISIBILITY.apply(this.player);
		this.speed.apply(this.player);
	}

	private void setWarningDistance(final int distance) {
		WorldBorder border = this.player.getWorldBorder();
		if (border == null) {
			final WorldBorder original = this.origin.getWorldBorder();

			border = Bukkit.createWorldBorder();
			border.setCenter(original.getCenter());
			border.setSize(original.getSize());
		}

		border.setWarningDistance(distance);
		this.player.setWorldBorder(border);
	}

	private void updateAll() {
		if (!this.player.isOnline())
			return;

		final Map<EquipmentSlot, ItemStack> equipment = new HashMap<>(NO_EQUIPMENT);
		if (this.isRemoved()) {
			final EntityEquipment e = this.player.getEquipment();
			for (final EquipmentSlot slot : EquipmentSlot.values())
				equipment.put(slot, e.getItem(slot));
		}

		Bukkit.getOnlinePlayers().stream()
				.filter(p -> !p.equals(this.player))
				.forEach(p -> p.sendEquipmentChange(this.player, equipment));
	}

	@Override
	public void remove() {
		super.remove();
		this.bPlayer.addCooldown(this);

		if (this.player.isOnline()) {
			this.updateAll();
			Audience.audience(this.player).hideBossBar(this.status);
			this.player.setWorldBorder(this.player.getWorld().getWorldBorder());
			this.player.getWorld().playSound(this.player.getLocation(), Sound.ITEM_TRIDENT_RETURN, 1.35f, 0.1f);
		}
	}

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
		return this.cooldown;
	}

	@Override
	public String getName() {
		return "ChameleonSuit";
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

	}

	@Override
	public String getAuthor() {
		return "ProjectCoco";
	}

	@Override
	public String getVersion() {
		return null;
	}
}
