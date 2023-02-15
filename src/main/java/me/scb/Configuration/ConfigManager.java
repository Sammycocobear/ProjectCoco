package me.scb.Configuration;

import com.projectkorra.projectkorra.ability.CoreAbility;
import me.scb.Abilities.Earth.Sand.SandTornado;
import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    public static Config configPath;
    public static final FileConfiguration config = ProjectCoco.getPlugin().getConfig();
    public ConfigManager() {
        this.defaults();
        configPath = new Config("config.yml");
        this.deathmessages();
    }

    public static FileConfiguration getConfig(){
        return config;
    }


    private void deathmessages(){


    }


    public void defaults(){
        FileConfiguration config = getConfig();
        String path = "Abilities.Sand.";
        config.addDefault(path + "SandTornado.Cooldown",5000);
        config.addDefault(path + "SandTornado.SourceRange",10);
        config.addDefault(path + "SandTornado.Range",20);
        config.addDefault(path + "SandTornado.ShootSpeed",.5);
        config.addDefault(path + "SandTornado.RideSpeed",.5);
        config.addDefault(path + "SandTornado.SeatingSpeed",.5);
        config.addDefault(path + "SandTornado.RecallSpeed",.5);
        config.addDefault(path + "SandTornado.BreakTime",500);
        config.addDefault(path + "SandTornado.RideHeight",5);
        config.addDefault(path + "SandTornado.RecallSpeed",1);
        config.addDefault(path + "SandTornado.RecallSpeed",1);

        config.addDefault(path + "QuickSand.SourceRange",15);
        config.addDefault(path + "QuickSand.Cooldown",5000);
        config.addDefault(path + "QuickSand.Radius",5);
        config.addDefault(path + "QuickSand.RadiusIncreaseDelay",250);

        path = "Abilities.Chi.";
        config.addDefault(path + "ChameleonSuit.Cooldown", 6000);
        config.addDefault(path + "ChameleonSuit.DetectionDistance", 3.0);
        config.addDefault(path + "ChameleonSuit.DisengageOnHit", true);
        config.addDefault(path + "ChameleonSuit.SpeedPotency", 4);

        config.options().copyDefaults(true);
        ProjectCoco.getPlugin().saveConfig();

    }

}
