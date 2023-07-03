package me.scb.Abilities.Water.Plant;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.PlantAbility;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.TempBlock;
import de.slikey.effectlib.effect.VortexEffect;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class TendrilTwist extends PlantAbility implements AddonAbility {
    private Location location;
    public boolean hasShot = false;
    private Block sourceBlock;
    private TempBlock temp;
    private boolean climbingUp = false,climbingDown = false;
    private TendrilEffect effect = null;
    private double speed = ConfigManager.getConfig().getDouble("Abilities.Plant.TendrilTwist.Speed");
    private double selectRange = ConfigManager.getConfig().getDouble("Abilities.Plant.TendrilTwist.SelectRange");
    private double damage = ConfigManager.getConfig().getDouble("Abilities.Plant.TendrilTwist.Damage");
    private double hitbox = ConfigManager.getConfig().getDouble("Abilities.Plant.TendrilTwist.Hitbox");
    private double range = ConfigManager.getConfig().getDouble("Abilities.Plant.TendrilTwist.Range");
    public double radiusChange = ConfigManager.getConfig().getDouble("Abilities.Plant.TendrilTwist.SqueezeSpeed");
    private int maxSqueezes = ConfigManager.getConfig().getInt("Abilities.Plant.TendrilTwist.Squeezes");
    private static final Particle.DustTransition dust = new Particle.DustTransition(Color.fromRGB(9,49,2),Color.fromRGB(224,255,219),1);
    private double distance = 0;

    public TendrilTwist(Player player) {
        super(player);
        final TendrilTwist t = CoreAbility.getAbility(player,getClass());
        if (t != null && !t.hasShot) {
            t.remove();
        }
        if (bPlayer.isOnCooldown(this) || ! bPlayer.canBend(this)) return;
        sourceBlock = BlockSource.getWaterSourceBlock(player, selectRange, ClickType.SHIFT_DOWN, false, false, true);
        if (sourceBlock == null) return;
        start();
    }

    public void setShot(){
        if (hasShot)return;
        hasShot = true;
        location = sourceBlock.getLocation().add(.5,0.1,.5);
        Location playerLoc = player.getLocation();
        playerLoc.setPitch(0);
        location.setDirection(playerLoc.getDirection());
        temp = new TempBlock(sourceBlock,Material.AIR);
        bPlayer.addCooldown(this,3000);
    }

    private boolean climb(Location next, Location location) {
        Block above = next.getBlock();
        if (!climbingDown && !isTransparent(above)) {
            above = location.clone().add(location.getDirection().multiply(-speed/4)).getBlock();
            location.add(0, speed/4, 0);
            next.add(0, speed/4, 0);
            climbingUp = true;
            return above.isPassable() || above.getType().isAir();
        } else if (!climbingUp && isTransparent(next.clone().subtract(0,speed/4,0).getBlock())) {
            location.subtract(0, speed/4, 0);
            next.subtract(0, speed/4, 0);
            climbingDown = true;
            Location before = location.clone().add(location.getDirection().multiply(-speed/4));
            return !before.getBlock().isPassable();
        }
        climbingUp = false;
        climbingDown = false;
        return true;
    }


    public void doDamage() {
        if (effect != null) return;
        final LivingEntity livingEntity = GeneralMethods.getClosestLivingEntity(location,hitbox);
        if ((!climbingUp && !climbingDown) && livingEntity != null && !livingEntity.equals(player) && !(livingEntity instanceof ArmorStand) && !GeneralMethods.isObstructed(location,livingEntity.getLocation()) ){
            effect = new TendrilEffect(livingEntity);
        }
    }

    @Override
    public void progress() {
        if (!player.isOnline() || player.isDead()) {
            remove();
            return;
        }else if (distance >= range){
            remove();
            return;
        }else if (GeneralMethods.isRegionProtectedFromBuild(this,location)){
            remove();
            return;
        }

        if (!hasShot){
            if (!bPlayer.getBoundAbilityName().equals(getName())){
                remove();
                return;
            }
            playFocusWaterEffect(sourceBlock);
        }else {

            if (effect!= null && !effect.doEffect()){
                remove();
                return;
            }

            if (effect != null) return;
            for (int i = 0; i < 4; i++) {
                Location nextLocation = location.clone();
                if ((!climbingUp && !climbingDown)) {
                    nextLocation.add(location.getDirection().multiply(speed / 4));
                }

                if (!climb(nextLocation,location)) {
                    remove();
                    return;
                }
                if ((!climbingUp && !climbingDown)) {
                    location.add(location.getDirection().multiply(speed / 4));
                    distance += speed/4;
                }

                location.subtract(location.getDirection().multiply(speed / 4));
                location.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, location, 2, .125, .125, .125, 0, dust);
                location.add(location.getDirection().multiply(speed / 4));
            }


            if (getCurrentTick() % 2 == 0){
                location.getWorld().spawnParticle(Particle.BLOCK_CRACK, location, 5, .125, .125, .125, .1, Material.SPRUCE_LEAVES.createBlockData());

            }

            doDamage();

        }

    }

    @Override
    public void remove() {
        super.remove();
        if (temp != null){
            temp.revertBlock();
        }
        bPlayer.addCooldown(this);
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
        return "TendrilTwist";
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

    private void doDamage(LivingEntity entity) {
        DamageHandler.damageEntity(entity,player,damage,this);

    }

    private class TendrilEffect {
        private LivingEntity entity;
        private int step = 0;
        private double radius;
        private boolean done, rInc = false;
        private int squeezes = 0;

        public TendrilEffect(LivingEntity entity) {
            this.entity = entity;
            radius = entity.getWidth();
        }

        public Entity getEntity(){
            return entity;
        }

        public boolean doEffect() {
            if (!done && step * .0625 >= entity.getHeight()) {
                done = true;
            }
            if (!done) {
                step++;

                Location spawn = entity.getLocation();
                for (int x = 0; x < 2; x++) {
                    for (int i = 0; i < 2; i++) {
                        double angle = step * radius + (2 * Math.PI * i / 2);
                        Vector v = new Vector(Math.cos(angle) * radius, step * .0625, Math.sin(angle) * radius);
                        spawn.add(v);
                        spawn.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, spawn, 1, 0, 0, 0, 0, dust);
                        spawn.subtract(v);
                    }
                }
            }else {
                step = 0;
                while (step * 0.0625 < entity.getHeight()) {
                    Location spawn = entity.getLocation();
                    for (int x = 0; x < 2 && getCurrentTick() % 2 == 0; x++) {
                        for (int i = 0; i < 2; i++) {
                            double angle = step * radius + (2 * Math.PI * i / 2);
                            Vector v = new Vector(Math.cos(angle) * radius, step * .0625, Math.sin(angle) * radius);
                            spawn.add(v);
                            spawn.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, spawn, 1, 0, 0, 0, 0, dust);
                            spawn.subtract(v);
                        }
                    }
                    step++;
                }


                if (rInc) {
                    radius += radiusChange;
                    radius = Math.min(radius, entity.getWidth());
                    if (radius >= entity.getWidth()) {
                        rInc = !rInc;
                    }
                } else {
                    radius -= radiusChange;
                    radius = Math.max(radius, entity.getWidth()/2);
                    if (radius <= entity.getWidth()/2) {
                        rInc = true;
                        player.getWorld().playSound(entity.getLocation(), Sound.BLOCK_VINE_PLACE,1f,.5f);
                        doDamage(entity);
                        if (++squeezes >= maxSqueezes) {
                            return false;
                        }
                    }

                }

                if (entity.isDead()){
                    return false;
                }




            }
            return true;
        }

    }

    public String getInstructions(){
        return "Sneak at a plant to source, them left click to shoot out the Tendril.";
    }

    public String getDescription(){
        return "This move summons a plant tendril that will climb and move across any terrain, once the tendril finds an entity it will swirl around them and do damage";
    }
}
