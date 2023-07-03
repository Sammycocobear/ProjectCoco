package me.scb.Abilities.Earth.Sand;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.SandAbility;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import me.scb.Utils.FallDamageRemoval;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


public class SandTornado extends SandAbility implements AddonAbility {
    private final long cooldown = ConfigManager.getConfig().getLong("Abilities.Sand.SandTornado.Cooldown");
    private final double range = ConfigManager.getConfig().getDouble("Abilities.Sand.SandTornado.Range");
    private final double shootSpeed = ConfigManager.getConfig().getDouble("Abilities.Sand.SandTornado.ShootSpeed");
    private final double backSpeed = ConfigManager.getConfig().getDouble("Abilities.Sand.SandTornado.RecallSpeed");
    private final double rideHeight = ConfigManager.getConfig().getDouble("Abilities.Sand.SandTornado.RideHeight");
    private final double seatingSpeed = ConfigManager.getConfig().getDouble("Abilities.Sand.SandTornado.SeatingSpeed");
    private final double rideSpeed = ConfigManager.getConfig().getDouble("Abilities.Sand.SandTornado.RideSpeed");
    private final long breakTime = ConfigManager.getConfig().getLong("Abilities.Sand.SandTornado.BreakTime");
    private final int sourceRange = ConfigManager.getConfig().getInt("Abilities.Sand.SandTornado.SourceRange");

    private boolean isRide,isSeated;
    private boolean hasShot;
    private double angle,angleIncrease;
    private double yRadius;
    private double xRadius;
    private Location loc;
    private Vector direction;
    private double distance;
    private long startShift = 0;
    private boolean foundEntity = false;
    private Entity affectedEntity = null;
    private Vector velocity;
    public SandTornado(Player player) {
        super(player);
        angle = 0;
        angleIncrease = .25;
        xRadius = 7;
        yRadius = 1.25;
        final Block block = player.getTargetBlockExact(sourceRange);
        if (!isSand(block)) return;
        loc = block.getLocation().add(.5,1,.5);
        start();
    }

    public void setVariables(boolean isShift){
        if (direction != null) return;
        isRide = isShift;
        Location eyeLoc = GeneralMethods.getTargetedLocation(player,sourceRange);
        eyeLoc.setPitch(0);
        direction = eyeLoc.getDirection().multiply(isRide ? rideSpeed : shootSpeed);
        if (!isRide){
            hasShot = true;
        }else{
            setSeated();
            velocity = direction;
        }


    }

    @Override
    public void progress() {
        if (System.currentTimeMillis() - getStartTime() >= 5000){
            remove();
            return;
        } else if (distance >= range) {
            remove();
            return;
        }else if (foundEntity && loc.distanceSquared(player.getLocation()) < 2){
            remove();
            return;
        }

        drawParticles();
        if (!isSeated && isRide){
            setSeated();
            return;
        }else if(hasShot){
            doDamage();
        }
    }


    public void drawParticles(){
        for (int i = 0; i < 360; i += 10) {
            double x = (Math.toRadians(i / 30.0) * Math.cos(angle)) * xRadius;
            double y = i * (yRadius/100);
            double z = (Math.toRadians(i / 30.0) * Math.sin(angle)) * xRadius;
            Vector vector = new Vector(x, y, z);
            loc.add(vector);
            //player.getLocation().getWorld().spawnParticle(Particle.REDSTONE,loc,1,0,0,0,0,new Particle.DustOptions(Color.WHITE,.5f));
            player.getWorld().spawnParticle(Particle.FALLING_DUST, loc, 1, 0, 0, 0, 0, Material.END_STONE.createBlockData());
            loc.subtract(vector);
            angle += angleIncrease;
        }
        if (isRide){
            doRideAdvanceLocation();
        }else{
            advanceLocation();
        }
    }

    public void doRideAdvanceLocation(){
        if (!isSeated) return;
        Location eyeLoc = player.getEyeLocation();
        eyeLoc.setPitch(0);
        direction = eyeLoc.getDirection().multiply(rideSpeed);
        player.setVelocity(velocity);
        velocity = direction;
        loc.add(direction);
        distance+=rideSpeed;
        if (!climb()){
            remove();
            return;
        }
    }

    public void advanceLocation(){
        if (!hasShot) return;
        if (player.isSneaking() && !foundEntity){
            Location eyeLoc = player.getEyeLocation();
            eyeLoc.setPitch(0);
            direction = eyeLoc.getDirection().multiply(isRide ? rideSpeed : shootSpeed);
        }
        loc.add(direction);
        distance+= affectedEntity != null ? backSpeed : shootSpeed;
        if (!climb()){
            remove();
            return;
        }
    }

    private boolean climb() {
        Block above = loc.getBlock();

        if (!isTransparent(above)) {
            above = loc.getBlock().getRelative(BlockFace.UP);
            loc.add(0, 1, 0);
            if (isRide) {
                velocity.setY(1);
            }
            return GeneralMethods.isSolid(loc.getBlock().getRelative(BlockFace.DOWN)) && isTransparent(above);
        } else if (isTransparent(loc.getBlock().getRelative(BlockFace.DOWN)) ) {
            loc.add(0, -1, 0);
            if (isRide) {
                velocity.setY(-1);
            }
            return GeneralMethods.isSolid(loc.getBlock().getRelative(BlockFace.DOWN));
        }

        return true;
    }



    public String getInstructions(){
        return "Sneak on a sand-bendable block to source and then left click to summon the tornado. Once the tornado is summoned you can sneak to ride it or click to shoot it. If you click you can sneak to redirect it.";
    }

    public String getDescription(){
        return "Sneak on a sand-bendable block to mark it as the source, then left-click to summon a sand tornado. Once the tornado is summoned, you can sneak to ride it or left-click to shoot it. If the shot tornado captures an entity, it will pull them towards you. However, the entity can sneak to escape the sand tornado.";
    }

    public void doDamage(){
        if (foundEntity) {
            affectEntity();
            return;
        }
        for (int i = 0; i < yRadius * 2; i++) {
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(loc.clone().add(0,i,0),1)){
                if (AbilityUtils.isInValidEntity(entity,player)) continue;
                foundEntity = true;
                affectedEntity = entity;
                FallDamageRemoval.addFallDamageCap((LivingEntity) affectedEntity,0);
            }
        }
    }

    boolean isShifting = false;
    public void affectEntity() {
        if (!foundEntity) return;
        this.direction = player.getLocation().toVector().subtract(
                affectedEntity.getLocation().toVector()).multiply(backSpeed / 10);
        //TODO make sure they dont go above height
        affectedEntity.setVelocity(direction.setY(affectedEntity.getLocation().getY() - loc.clone().add(0,rideHeight,0).getY() > 0 ? -(seatingSpeed/10) : seatingSpeed/10));

        if (!(affectedEntity instanceof Player)) return;
        Player entity = (Player) affectedEntity;
        if (entity.isSneaking()) {
            if (!isShifting) {
                startShift = System.currentTimeMillis();
                isShifting = true;
            }
            if (System.currentTimeMillis() - startShift >= breakTime) {
                remove();
                return;
            }
        } else {
            if (startShift != 0) {
                isShifting = false;
                startShift = 0;
            }
        }
    }

    public void setSeated(){
        if (isSeated) return;
        final Location targetLoc = loc.clone().add(0,rideHeight,0);
        player.setVelocity(targetLoc.toVector().subtract(player.getLocation().toVector()).multiply(seatingSpeed / 10));
        if (player.getLocation().distanceSquared(targetLoc) < 2){
            isSeated =true;
        }
    }

    @Override
    public void remove() {
        super.remove();
        bPlayer.addCooldown(this,cooldown);
    }

    @Override
    public boolean isSneakAbility() {
        return false;
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public String getName() {
        return "SandTornado";
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
        return ProjectCoco.getAuthor();
    }

    @Override
    public String getVersion() {
        return ProjectCoco.getVersion();
    }
}
