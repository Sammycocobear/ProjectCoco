package me.scb.Abilities.Water.Plant.SeedSummoner;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.PlantAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.TempBlock;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import me.scb.Utils.RainbowColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeaShooter extends PlantAbility implements AddonAbility {

    private List<Shot> shots = new ArrayList<>();
    private int peaCount = ConfigManager.getConfig().getInt("Abilities.Plant.SeedSummoner.PeaShooter.PeaCount");
    private double selectRange = ConfigManager.getConfig().getDouble("Abilities.Plant.SeedSummoner.PeaShooter.SelectRange");
    private double range = ConfigManager.getConfig().getDouble("Abilities.Plant.SeedSummoner.PeaShooter.Range");
    private double hitbox = ConfigManager.getConfig().getDouble("Abilities.Plant.SeedSummoner.PeaShooter.Hitbox");
    private double damage = ConfigManager.getConfig().getDouble("Abilities.Plant.SeedSummoner.PeaShooter.Damage");
    private double speed = ConfigManager.getConfig().getDouble("Abilities.Plant.SeedSummoner.PeaShooter.Speed");

    private boolean hasStarted = false;
    private Block sourceBlock;
    private Location startLocation;
    private TempBlock stem;
    private TempBlock farm;

    private Location origin;
    private long next;
    private static final Particle.DustOptions greenColor = new Particle.DustOptions(Color.fromRGB(102, 255, 102),1);
    private static final FixedMetadataValue stopGrowingMelon = new FixedMetadataValue(ProjectCoco.getPlugin(),1);
    private List<Entity> entityList = new ArrayList<>();
    private int index = 0;

    public PeaShooter(Player player) {
        super(player);
        final PeaShooter c = CoreAbility.getAbility(player,getClass());
        if (c != null && !c.hasStarted) {
            final SeedSummoner s = CoreAbility.getAbility(player,SeedSummoner.class);
            if (s != null){
                s.removeAbility(c);
            }
            c.remove();
        }

        if (bPlayer.isOnCooldown(this) || !bPlayer.canBendIgnoreBinds(this)) {
            return;
        }
        sourceBlock = GeneralMethods.getTargetedLocation(player, selectRange)
                .add(player.getLocation().getDirection().multiply(.5)).getBlock(); //get a little bit infront of the targeted location to get inside of the block
        if (!isPlant(sourceBlock)) return;
        start();

    }

    public void makeSphere(Location location){
        //instance variables probably

        for (int i = 0; i < 5; i++) {
            if (RainbowColor.playParticles(player,location.clone().add(AbilityUtils.getRandomVector().multiply(.5)),index)) {
                location.getWorld().spawnParticle(Particle.REDSTONE, location.clone().add(AbilityUtils.getRandomVector().multiply(.5)),
                        1, 0, 0,
                        0, 0, greenColor);
            }
        }
        index++;
    }


    public void makePeaShooter(){
        farm = new TempBlock(sourceBlock.getRelative(BlockFace.DOWN),Material.FARMLAND);
        BlockData baseState = Material.MELON_STEM.createBlockData();
        Ageable ageable = (Ageable) baseState;
        ageable.setAge(ageable.getMaximumAge());
        stem = new TempBlock(sourceBlock, ageable);
        sourceBlock.setMetadata("ProjectCoco://PeaShooter://Melon",stopGrowingMelon);

        startLocation = stem.getLocation().add(0.5,1,.5);
        startLocation.setYaw(origin.getYaw() + 180); //turns it towards the player
        startLocation.setPitch(0); //straight
        makeSphere(startLocation);
    }



    public void makeNewShot(){
        shots.add(new Shot());
    }


    public void setShot(){
        if (hasStarted) return;
        hasStarted = true;
        origin = player.getLocation();
        makePeaShooter();
    }

    @Override
    public void progress() {
        if (System.currentTimeMillis() - getStartTime() >= 5000){ //TODO CHANGE TO RANGE
            remove();
            return;
        }

        if (!hasStarted){
            playFocusWaterEffect(sourceBlock);
            return;
        }
        if (System.currentTimeMillis() >= next){
            next = System.currentTimeMillis() + 500;
            makeNewShot();
        }
        shots.removeIf(Shot::progress);
        makeSphere(startLocation);
    }

    @Override
    public void remove() {
        super.remove();
        shots.forEach(Shot::remove); //todo
        if (stem != null) {
            stem.revertBlock();
        }

        if (farm != null){
            farm.revertBlock();
        }
        if (sourceBlock.hasMetadata("ProjectCoco://PeaShooter://Melon")) {
            sourceBlock.removeMetadata("ProjectCoco://PeaShooter://Melon", ProjectCoco.getPlugin());
        }
    }

    public void doDamage(Location location){
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location,hitbox)){
            if (AbilityUtils.isInValidEntity(entity,player) || entityList.contains(entity)) continue;
            DamageHandler.damageEntity(entity,player,damage,this);
            entityList.add(entity);
        }
    }

    @Override
    public boolean isHiddenAbility() {
        return true;
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
        return "PeaShooter";
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
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }


    public class Shot{
        private final Map<Item, Vector> peas = new HashMap<>();

        public Shot(){
            makePeas();
            progress();
        }

        public boolean progress(){
            for (Item item : peas.keySet()) {
                if (item.getLocation().distanceSquared(startLocation) >= (range * range)){
                    remove();
                    return true;
                }
                
                if (item.isDead()) continue;
                doDamage(item.getLocation());
                item.setVelocity(peas.get(item));
                RainbowColor.playParticles(player,item.getLocation(),index,.5,.5,.5);
            }

            return false;
        }



        public void makePeas(){
            for (int x = -peaCount/2; x < peaCount/2; x++) {
                final double angleRadians = Math.toRadians(x * 2.5);
                final Vector dir = GeneralMethods.getDirection(startLocation,origin).normalize();
                final double xComp = dir.getX();
                final double zComp = dir.getZ();
                final double rotatedXComp = xComp * Math.cos(angleRadians) - zComp * Math.sin(angleRadians);
                final double rotatedZComp = xComp * Math.sin(angleRadians) + zComp * Math.cos(angleRadians);
                Item peaItem = AbilityUtils.dropItem(startLocation,Material.SLIME_BALL);

                Vector rotatedDir = dir.clone();
                rotatedDir.setX(rotatedXComp);
                rotatedDir.setZ(rotatedZComp);
                peas.put(peaItem, rotatedDir.multiply(speed));
            }
        }

        public void remove(){
            peas.keySet().forEach(Entity::remove);
        }
    }
}
