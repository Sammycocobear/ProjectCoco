package me.scb;

import com.projectkorra.projectkorra.ability.CoreAbility;
import me.scb.Configuration.ConfigManager;
import me.scb.Listener.AbilityListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;

public final class ProjectCoco extends JavaPlugin {
    public static ProjectCoco plugin;
    @Override
    public void onEnable() {
        plugin = this;
        new ConfigManager();
        CoreAbility.registerPluginAbilities(this,"me.scb.Abilities");
        getPlugin().getServer().getPluginManager().registerEvents(new AbilityListener(),plugin);
    }
    public static ProjectCoco getPlugin(){
        return plugin;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }




}
