package me.scb.Abilities.Fire.Lightning;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.TempBlock;
import me.scb.Utils.RainbowColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

public class Test extends FireAbility implements AddonAbility {
    Location l;
    public Test test = this;

    public Test(Player player) {
        super(player);
        player.getLocation().getWorld().spawnEntity(player.getEyeLocation(), EntityType.EGG).setVelocity(player.getLocation().getDirection());


        start();
    }

    int ticks = 0;
    double ar = 0;
    @Override
    public void progress() {
        if (System.currentTimeMillis() - getStartTime() >= 5500) {
            remove();
            return;
        }

        double x, z;
        x = Math.sin(ar);
        z = Math.cos(ar);
        ar += .1;
        Vector vector = new Vector(x,0,z);
        spawnRainbowParticle(player.getLocation().add(vector));

    }

    private final int[] hexs = {
            0xD81919,0xE08632,0xFAF135,0x4AC71C,0x41A6CF,0xAB17D4
    };
    private int currIndex = 0;
    private double currRatio;
    private boolean increment = true;
    private final double colorCycleSpeed = .1;

    public void spawnRainbowParticle(Location location){

        if (currRatio == 1 && increment){
            currRatio = 0;
            currIndex++;
        }else if (currRatio == 0 && !increment){
            currRatio = 1;
            currIndex--;
        }

        if ((currIndex == hexs.length - 1 && increment) || (currIndex == 0 && !increment)) {
            increment = !increment;
            if (!increment){
                currRatio = 1;
            }
        }

        currRatio = increment ? Math.min(currRatio += colorCycleSpeed, 1) : Math.max(currRatio -= colorCycleSpeed, 0);
        int to,from;
        to = increment ? hexs[currIndex + 1] : hexs[currIndex];
        from = increment ? hexs[currIndex] : hexs[currIndex - 1];

        Color color = Color.fromRGB(lerpColor(from,to,currRatio));

        location.getWorld().spawnParticle(Particle.REDSTONE,location,
                1,0,0,0,new Particle.DustOptions(color,.8f));
    }


    public static int lerpColor(int pFrom, int pTo, double pRatio) {
        final int ar = (pFrom & 0xFF0000) >> 16;
        final int ag = (pFrom & 0x00FF00) >> 8;
        final int ab = (pFrom & 0x0000FF);

        final int br = (pTo & 0xFF0000) >> 16;
        final int bg = (pTo & 0x00FF00) >> 8;
        final int bb = (pTo & 0x0000FF);

        final int rr = (int) (ar + pRatio * (br - ar));
        final int rg = (int) (ag + pRatio * (bg - ag));
        final int rb = (int) (ab + pRatio * (bb - ab));

        return (rr << 16) + (rg << 8) + rb;
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
        return "Test";
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

}


