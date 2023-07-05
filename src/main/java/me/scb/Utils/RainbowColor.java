package me.scb.Utils;

import me.scb.Listener.EasterEggListener;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class RainbowColor {
    private static final Particle.DustOptions white =
            new Particle.DustOptions(Color.fromRGB(0xffffff), 1.0f);
    private static final Particle.DustTransition[] dustTransition;

    private static final int center = 200;
    private static final int width = 55;

    private static Color getRainbowColour(final int width) {
        return Color.fromRGB(
                (int) (Math.sin(0.3*width)* RainbowColor.width + center),
                (int) (Math.sin(0.3*width+2.0)* RainbowColor.width + center),
                (int) (Math.sin(0.3*width+4)* RainbowColor.width + center));
    }

    static {
        dustTransition = new Particle.DustTransition[width];

        for (int width = 0; width < RainbowColor.width; width++) {
            final Color from = width == 0 ? getRainbowColour(RainbowColor.width) : dustTransition[width-1].getToColor();
            final Color to = getRainbowColour(width);

            dustTransition[width] = new Particle.DustTransition(from, to, white.getSize());
        }
    }


    public static boolean playParticles(Player player, Location loc, int index) {
        if (!EasterEggListener.hasEasterEgg(player)) {
            return true;
        }
        //final int index = (int) (System.currentTimeMillis() % RAINBOW_PARTICLES.length);
        loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, dustTransition[index % width]);
        return false;
    }

    public static boolean playParticles(Player player, Location loc, int index, double x, double y, double z) {
        if (!EasterEggListener.hasEasterEgg(player)) {
            return true;
        }
        //final int index = (int) (System.currentTimeMillis() % RAINBOW_PARTICLES.length);
        loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, x, y, z, dustTransition[index % width]);
        return false;
    }


    public static boolean playParticles(Player player, Location loc, int index, double x, double y, double z, float size) {
        if (!EasterEggListener.hasEasterEgg(player)) {
            return true;
        }
        //final int index = (int) (System.currentTimeMillis() % RAINBOW_PARTICLES.length);
        loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, x, y, z, new Particle.DustOptions(dustTransition[index % width].getColor(),size));
        return false;
    }

}
