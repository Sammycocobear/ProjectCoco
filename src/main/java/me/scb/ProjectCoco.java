package me.scb;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;
import de.slikey.effectlib.EffectManager;
import me.scb.Configuration.ConfigManager;
import me.scb.Listener.AbilityListener;
import me.scb.Listener.DamageHandlerListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;

public final class ProjectCoco extends JavaPlugin {
    private static ProjectCoco plugin;
    private static EffectManager effectManager;

    @Override
    public void onEnable() {
        plugin = this;
        effectManager = new EffectManager(ProjectCoco.getPlugin());
        new ConfigManager();
        CoreAbility.registerPluginAbilities(this,"me.scb.Abilities");
        getPlugin().getServer().getPluginManager().registerEvents(new AbilityListener(),plugin);
        getPlugin().getServer().getPluginManager().registerEvents(new DamageHandlerListener(),plugin);

    }
    public static ProjectCoco getPlugin(){
        return plugin;
    }
    public static EffectManager getEffectManager(){
        return effectManager;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }




}
