package me.scb.Configuration;

import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.FallingBlock;

import java.util.ArrayList;
import java.util.List;

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

        config.addDefault(path + "SandPad.Radius",4);
        config.addDefault(path + "SandPad.Duration",5000);
        config.addDefault(path + "SandPad.Height",10);
        config.addDefault(path + "SandPad.Cooldown",7000);
        config.addDefault(path + "SandPad.FailedCooldown",3000);
        config.addDefault(path + "SandPad.RiseSpeed",1.25);


        path = "Abilities.Combustion.";

        config.addDefault(path + "CombustionBomb.Gravity",.05);
        config.addDefault(path + "CombustionBomb.Speed",.25);
        config.addDefault(path + "CombustionBomb.MaxHeight",1);
        config.addDefault(path + "CombustionBomb.EndExplosion.IncreaseSpeed",.5);
        config.addDefault(path + "CombustionBomb.EndExplosion.MaxRadius",5);
        config.addDefault(path + "CombustionBomb.EndExplosion.Spheres",6);
        config.addDefault(path + "CombustionBomb.Hitbox",1.5);
        config.addDefault(path + "CombustionBomb.Damage",2);
        config.addDefault(path + "CombustionBomb.Cooldown",7000);
        config.addDefault(path + "CombustionBomb.MaxBounces",3);

        path = "Abilities.Lightning.";
        config.addDefault(path + "Railgun.MinimumChargeTime",1000);
        config.addDefault(path + "Railgun.MaxChargeTime",2000);
        config.addDefault(path + "Railgun.Hitbox",1.5);
        config.addDefault(path + "Railgun.Damage",3);
        config.addDefault(path + "Railgun.RangeMultiplier",15);
        config.addDefault(path + "Railgun.Cooldown",5000);

        config.addDefault(path + "ThunderStorm.Radius",5);
        config.addDefault(path + "ThunderStorm.SourceRange",15);
        config.addDefault(path + "ThunderStorm.Height",5);
        config.addDefault(path + "ThunderStorm.Cooldown",5000);

        path = "Abilities.BlueFire.";

        config.addDefault(path + "BlueFireOrbs.Cooldown",5000);
        config.addDefault(path + "BlueFireOrbs.OrbCount",5);
        config.addDefault(path + "BlueFireOrbs.OrbRange",15);
        config.addDefault(path + "BlueFireOrbs.IsControllable",true);
        config.addDefault(path + "BlueFireOrbs.Radius",5);

        config.addDefault(path + "BlueFireOrbs.Speed",1);

        path = "Abilities.Blood.";
        config.addDefault(path + "BloodRush.Range",20);
        config.addDefault(path + "BloodBlink.SourceRange",20);
        config.addDefault(path + "BloodBlink.SpeedAmplifier",2);

        config.addDefault(path + "BloodPool.SourceRange",15);
        config.addDefault(path + "BloodPool.Range",10);
        config.addDefault(path + "BloodPool.MaxHits",3);
        config.addDefault(path + "BloodPool.Damage",1);
        config.addDefault(path + "BloodPool.Cooldown",5000);
        config.addDefault(path + "BloodPool.Radius",2);
        config.addDefault(path + "BloodPool.MaxPools",3);
        config.addDefault(path + "BloodPool.DamageDelay",1000);


        path = "Abilities.Ice.";
        config.addDefault(path + "IcyGrenade.SourceRange",20);

        config.addDefault(path + "Hail.SearchRange",15);
        config.addDefault(path + "Hail.Damage",2);
        config.addDefault(path + "Hail.Cooldown",5000);
        config.addDefault(path + "Hail.DamageInterval",1000);
        config.addDefault(path + "Hail.SnowDuration",1000);
        config.addDefault(path + "Hail.SlownessDuration",1000);
        config.addDefault(path + "Hail.SlownessAmplifier",1000);

        path = "Abilities.Healing.";
        config.addDefault(path + "RefreshingRain.ChargeTime",1000);
        config.addDefault(path + "RefreshingRain.Duration",5000);
        config.addDefault(path + "RefreshingRain.HealingDelay",1000);
        config.addDefault(path + "RefreshingRain.HealingPerDelay",1);


        path = "Abilities.Water.";
        config.addDefault(path + "RainCloud.Radius",5);
        config.addDefault(path + "RainCloud.SourceRange",10);
        config.addDefault(path + "RainCloud.Cooldown",1000);
        config.addDefault(path + "RainCloud.Height",5);
        config.addDefault(path + "RainCloud.ApplyBoneMeal",true);
        config.addDefault(path + "RainCloud.Duration",5000);
        config.options().copyDefaults(true);
        ProjectCoco.getPlugin().saveConfig();

    }

}
