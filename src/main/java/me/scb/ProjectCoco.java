package me.scb;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import me.scb.Configuration.ConfigManager;
import me.scb.Listener.AbilityListener;
import me.scb.Listener.DamageHandlerListener;
import me.scb.Listener.EasterEggListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class ProjectCoco extends JavaPlugin {
    private static ProjectCoco plugin;
    private static final String author = "bbakaa";
    private static final String version = "PRE-RELEASE-1.0.0";

    public static boolean hasSound(){
        Element.SubElement[] subElements = Element.SubElement.getAddonSubElements();
        for (int i = 0; i < subElements.length; i++) {
            Element.SubElement e = subElements[i];
            if (e.getName().equalsIgnoreCase("sound")){
                return true;
            }
        }
        return false;
    }



    @Override
    public void onEnable() {
        plugin = this;
        new ConfigManager();
        CoreAbility.registerPluginAbilities(this,"me.scb.Abilities");
        getPlugin().getServer().getPluginManager().registerEvents(new AbilityListener(),plugin);
        getPlugin().getServer().getPluginManager().registerEvents(new DamageHandlerListener(),plugin);
        getPlugin().getServer().getPluginManager().registerEvents(new EasterEggListener(),plugin);



    }
    public static ProjectCoco getPlugin(){
        return plugin;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static final String getAuthor(){
        return author;
    }

    public static final String getVersion(){
        return version;
    }


}
