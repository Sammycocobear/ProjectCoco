package me.scb.Abilities.Earth.Sand;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.SandAbility;
import com.projectkorra.projectkorra.util.TempBlock;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SandPad extends SandAbility implements AddonAbility {
    private final int radius = ConfigManager.getConfig().getInt("Abilities.Sand.SandPad.Radius");
    private final long duration = ConfigManager.getConfig().getLong("Abilities.Sand.SandPad.Duration");
    private final double height = ConfigManager.getConfig().getDouble("Abilities.Sand.SandPad.Height");
    private final List<FallingBlock> sand = new ArrayList<>();
    private final long cooldown = ConfigManager.getConfig().getLong("Abilities.Sand.SandPad.Cooldown"),
            failedCooldown = ConfigManager.getConfig().getLong("Abilities.Sand.SandPad.FailedCooldown");
    private final double riseSpeed = ConfigManager.getConfig().getDouble("Abilities.Sand.SandPad.RiseSpeed");
    private final Vector riseVector = new Vector(0,riseSpeed,0);
    private final Vector entityVector = riseVector.clone().setY(riseSpeed + .25);
    private final double originY = player.getLocation().getY();
    private int count = 0;
    private boolean reachedMax = false;
    private long del = 0;
    private long riseFinishTime;
    private final List<TempBlock> sandstoneList = new ArrayList<>();
    public SandPad(Player player) {
        super(player);
        if (CoreAbility.hasAbility(player,getClass()) || bPlayer.isOnCooldown(this)) return;
        if (!isSand(player.getLocation().getBlock().getRelative(BlockFace.DOWN))) return;
        makeSand(player.getLocation());
        start();

    }

    public void makeSand(Location loc){
        for (Location location : GeneralMethods.getCircle(loc,radius,1,false,false,0)){
            Block b = location.getBlock();
            sand.add(AbilityUtils.createFallingBlock(b.getLocation().add(0.5,0.5,0.5),Material.SAND));
        }
    }


    @Override
    public void progress() {
        if (reachedMax && System.currentTimeMillis() - riseFinishTime >= duration){
            bPlayer.addCooldown(this,cooldown);
            for (TempBlock tempBlock : sandstoneList) {
                double randomx = ThreadLocalRandom.current().nextBoolean() ? ThreadLocalRandom.current().nextDouble()/2 : -ThreadLocalRandom.current().nextDouble()/2;
                double randomz = ThreadLocalRandom.current().nextBoolean() ? ThreadLocalRandom.current().nextDouble()/2 : -ThreadLocalRandom.current().nextDouble()/2;
                AbilityUtils.createFallingBlock(tempBlock.getLocation().add(0.5,0.5,0.5),Material.SANDSTONE).setVelocity(new Vector(randomx,-.1,randomz));
                tempBlock.getLocation().getWorld().spawnParticle(Particle.BLOCK_DUST,tempBlock.getLocation(),1,0,0,0,0,Material.SAND.createBlockData());
                player.sendBlockChange(tempBlock.getLocation(),tempBlock.getBlock().getBlockData());
                tempBlock.revertBlock();
            }
            remove();
            return;
        }

        if (!reachedMax) {
            List<Entity> entityList = null;
            for (FallingBlock fallingBlock : sand) {
                if (count++ % sand.size() == 0) {
                    if (fallingBlock.getLocation().getY() - originY >= height) {
                        riseFinishTime  = System.currentTimeMillis();
                        reachedMax = true;
                    }
                    for (Entity entity : entityList = GeneralMethods.getEntitiesAroundPoint(fallingBlock.getLocation(),radius)){
                        if (AbilityUtils.isInValidEntity(entity)) continue;
                        if (reachedMax){
                            entity.teleport(entity.getLocation().add(0,1.25,0));
                            continue;
                        }
                        entity.setVelocity(entityVector);
                    }
                    if (reachedMax) break;
                }


                if (fallingBlock.getLocation().getBlock().getRelative(BlockFace.UP).isSolid()) {
                    if (entityList != null){
                        entityList.forEach(e -> e.setVelocity(new Vector(0,-.5,0)));
                    }
                    bPlayer.addCooldown(this, failedCooldown);
                    sand.forEach(s -> {
                        double randomx = ThreadLocalRandom.current().nextBoolean() ? ThreadLocalRandom.current().nextDouble()/2 : -ThreadLocalRandom.current().nextDouble()/2;
                        double randomz = ThreadLocalRandom.current().nextBoolean() ? ThreadLocalRandom.current().nextDouble()/2 : -ThreadLocalRandom.current().nextDouble()/2;
                        s.setVelocity(new Vector(randomx, -.5, randomz));
                        s.getLocation().getWorld().spawnParticle(Particle.BLOCK_DUST,s.getLocation(),1,0,0,0,0,Material.SAND.createBlockData());
                    });
                    remove();
                    return;
                }
                fallingBlock.setVelocity(riseVector);
            }

            if (reachedMax){
                for (FallingBlock fallingBlock : sand){
                    sandstoneList.add(new TempBlock(fallingBlock.getLocation().getBlock(),Material.SANDSTONE.createBlockData()));
                }
                sand.forEach(Entity::remove);
                sand.clear();
            }

            return;
        }

        if (System.currentTimeMillis() >= del){
            del = System.currentTimeMillis() + duration/10;
            sandstoneList.forEach(s -> Bukkit.getOnlinePlayers().forEach(p -> createBlockBreakAnimation(s.getBlock(), p, progress)));
            progress++;
        }


    }

    int progress = 0;

    public void createBlockBreakAnimation(Block block,Player player,int stage){
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer blockBreakAnimPacket = protocolManager.createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
        blockBreakAnimPacket.getBlockPositionModifier().write(0, new BlockPosition(block.getX(), block.getY(), block.getZ()));
        blockBreakAnimPacket.getIntegers().write(0, (int) (Math.random() * 1000));
        blockBreakAnimPacket.getIntegers().write(1, stage);

        try {
            protocolManager.sendServerPacket(player, blockBreakAnimPacket);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove() {
        super.remove();
    }

    public String getInstructions(){
        return "Click while standing on-top of a sand-bendable block.";
    }

    public String getDescription(){
        return "While sneaking on a sand block, the SandPad ability will be activated, propelling you upwards into the air. After a certain duration, the pad will break, causing anyone standing on it to fall down.";
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
        return 0;
    }

    @Override
    public String getName() {
        return "SandPad";
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

    public String getVersion() {
        return ProjectCoco.getVersion();
    }

}
