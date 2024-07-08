package me.scb.Abilities.Earth.Lava;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.LavaAbility;
import com.projectkorra.projectkorra.util.TempBlock;
import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import me.scb.Utils.RainbowColor;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Volcano  extends LavaAbility implements AddonAbility {
    private int radius = 5;
    private Location source;
    private long next,delay;
    private int balls = 20;
    private float yaw = 0;
    private int currentBalls = 0;
    private int circles = 3;
    private Map<Location,FallingBlock> fallingBlocks = new HashMap<>();
    private static final Vector nullVector = new Vector(0,0,0);
    private List<LavaBall> ballList = new ArrayList<>();
    public Volcano(Player player) {
        super(player);
        source = GeneralMethods.getTargetedLocation(player,20);
        int x = 0;
        while (source.subtract(0,1,0).getBlock().getType().isAir() && x++ < 50);
        source = source.getBlock().getLocation().add(.5,1,.5);
        source.setYaw(player.getLocation().getYaw());
        delay = 20;
        createVolcano();
        start();
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public void createVolcano(){
        double radius = circles;
        Location location = source.clone();
        for (int i = 0; i < circles * 1.5; i++) {
            for (int j = 0; j < 25 - (6 + i); j++) {
                double a = Math.toRadians(360.0 / (25 - (6 + i))) * j;
                double x = (radius * Math.sin(a));
                double z = (radius * Math.cos(a));
                location.add(x, 0, z);
                FallingBlock block = AbilityUtils.createFallingBlock(location,ThreadLocalRandom.current().nextInt(5) == 3 ? Material.BROWN_CONCRETE : Material.BROWN_TERRACOTTA);
                block.setGravity(false);
                fallingBlocks.put(location.clone(),block);
                location.subtract(x, 0, z);
            }
            radius-=2/3.0;
            location.add(0,2/3.0,0);
        }
    }

    @Override
    public void progress() {
        if (currentBalls >= balls){
            if (ballList.isEmpty()) {
                remove();
                return;
            }
        }


        for (Location location : fallingBlocks.keySet()) {
            FallingBlock fallingBlock = fallingBlocks.get(location);
            fallingBlock.setVelocity(nullVector);
        }

        if (currentBalls < balls && System.currentTimeMillis() >= next && radius > 1){
            next = System.currentTimeMillis() + delay;
            Location location = source.clone();
            location.setYaw(location.getYaw() + yaw);
            yaw += 360.0/balls;
            ballList.add(new LavaBall(location));
            currentBalls++;
        }

        ballList.removeIf(LavaBall::shoot);


    }

    @Override
    public void remove() {
        super.remove();
        fallingBlocks.values().forEach(Entity::remove);
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
        return "Volcano";
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
    private double minRange = 2,maxRange = 10;
    private double speed = 3;
    private double height = 7;
    private float iterations = 90;
    private static final Particle.DustOptions DUST_OPTIONS = new Particle.DustOptions(Color.RED,1);
    public class LavaBall{
        private int currentIterations;
        private int index;
        private final Map<BlockDisplay, Location> blockDisplayLocationMap = new HashMap<>();
        private final Location location;
        private final Location toLocation;
        public LavaBall(Location location){
            this.location = location.clone().add(0,circles + .5,0);
            this.toLocation = location.clone();
            this.toLocation.setPitch(0);
            this.toLocation.add(toLocation.getDirection().multiply(ThreadLocalRandom.current().nextDouble(minRange,maxRange)));
            location.getWorld().playSound(location, Sound.ENTITY_SHULKER_SHOOT,1.5f,1.7f);
            location.getWorld().playSound(location, Sound.ENTITY_ZOMBIE_INFECT,1.5f,1.5f);
            for (int i = 0; i < 3; i++) {
                BlockDisplay display = (BlockDisplay)  location.getWorld().spawnEntity(this.location.clone().add(AbilityUtils.getRandomVector().multiply(.5)), EntityType.BLOCK_DISPLAY);
                display.setBlock(Material.MAGMA_BLOCK.createBlockData());
                Transformation transformation = new Transformation(display.getTransformation().getTranslation(),display.getTransformation().getLeftRotation(),new Vector3f(.75f,.75f,.75f),display.getTransformation().getRightRotation());
                display.setTransformation(transformation);
                display.setGravity(false);
                blockDisplayLocationMap.put(display,display.getLocation());
            }
            makeSphere();

        }
        public void makeSphere() {
            Vector vector = AbilityUtils.getRandomVector().multiply(.5);
            Location location = this.location.clone().add(vector);
            if (RainbowColor.playParticles(player, location, index, 0, 0, 0, 1)) {
                location.getWorld().spawnParticle(Particle.REDSTONE, location,
                        1, 0, 0, 0, 0, DUST_OPTIONS);
            }
            index++;
        }

        public boolean shoot(){
            if (currentIterations >= iterations){
                blockDisplayLocationMap.keySet().forEach(Entity::remove);
                return true;
            }
            updateLocation();
            return false;
        }

        public void updateLocation() {
            Vector link = toLocation.toVector().subtract(location.toVector());
            float length = (float) link.length();
            float pitch = (float) (4 * height / Math.pow(length, 2));

            currentIterations += speed;

            Vector v = link.clone().normalize().multiply(length * currentIterations / iterations);
            float x = ((float) currentIterations / iterations) * length - length / 2;
            float y = (float) (-pitch * Math.pow(x, 2) + height);
            location.add(v).add(0, y, 0);
            makeSphere();
            for (BlockDisplay display : blockDisplayLocationMap.keySet()){
                display.teleport(blockDisplayLocationMap.get(display).add(v).add(0,y,0));
                blockDisplayLocationMap.get(display).subtract(v).subtract(0,y,0);
            }
            location.getWorld().playSound(location,Sound.BLOCK_POINTED_DRIPSTONE_DRIP_LAVA,.5f,1);
            location.subtract(v).subtract(0, y, 0);

        }
    }
}
