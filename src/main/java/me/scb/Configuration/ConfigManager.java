package me.scb.Configuration;

import me.scb.ProjectCoco;
import me.scb.Utils.AbilityUtils;
import org.bukkit.Location;
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
        FileConfiguration config = com.projectkorra.projectkorra.configuration.ConfigManager.languageConfig.get();
        config.addDefault("Abilities.Air.SoundBarrier.DeathMessage","{victim} died when they collided with {attacker}.");
        config.addDefault("Abilities.Earth.SandTornado.DeathMessage","{victim} was overwhelmed by the relentless force of the sand tornado unleashed by {attacker}, meeting a swirling fate.");
        config.addDefault("Abilities.Fire.BlueFireOrbs.DeathMessage","{victim} was consumed by the fiery impact of the orb shot by {attacker}'s BlueFireOrbs ability, meeting a blazing end.");
        config.addDefault("Abilities.Fire.CombustionBomb.DeathMessage","{victim} perished in a chain reaction of explosive devastation caused by {attacker}'s CombustionBomb, leaving nothing but destruction in their wake.");
        config.addDefault("Abilities.Fire.Railgun.DeathMessage","{victim} was caught in the devastating blast of {attacker}'s RailGun teleportation, meeting a swift and electrifying demise.");
        config.addDefault("Abilities.Fire.ThunderStorm.DeathMessage","{victim} was struck down by the wrath of nature as {attacker} summoned a thunderstorm, where lightning strikes caused their demise.");
        config.addDefault("Abilities.Water.Hail.DeathMessage","{victim} was pummeled by hailstones unleashed by {attacker}, succumbing to the relentless barrage from above.");
        config.addDefault("Abilities.Water.TendrilTwist.DeathMessage","{victim} was squeezed to death by the tendrils controlled by {attacker}, ending in a twisted fate.");
        config.addDefault("Abilities.Water.ThornyBush.DeathMessage","{victim} met a thorny demise, pierced and ensnared by the sharp thorns of the bush summoned by {attacker}.");
        config.addDefault("Abilities.Water.BloodPool.DeathMessage","{victim} met a gruesome end as they drowned in the blood-soaked pool conjured by {attacker}.");
        com.projectkorra.projectkorra.configuration.ConfigManager.languageConfig.save();

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

        config.addDefault("Abilities.Lava.VolcanicJets.Jets",3);
        config.addDefault("Abilities.Lava.VolcanicJets.Range",12);
        config.addDefault("Abilities.Lava.VolcanicJets.Speed",1);
        config.addDefault("Abilities.Lava.VolcanicJets.Jet.Height",10);
        config.addDefault("Abilities.Lava.VolcanicJets.Jet.SpawnDelay",250);
        config.addDefault("Abilities.Lava.VolcanicJets.Jet.Cooldown",10000);

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
        config.addDefault(path + "ThunderStorm.Damage",2);
        config.addDefault(path + "ThunderStorm.Hotbox",1);

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
        config.addDefault(path + "BloodBlink.Duration",7000);
        config.addDefault(path + "BloodBlink.Cooldown",4000);
        config.addDefault(path + "BloodBlink.JumpVelocity",2);

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
        config.addDefault(path + "Hail.SlownessDuration",40);
        config.addDefault(path + "Hail.SlownessAmplifier",3);

        path = "Abilities.Healing.";
        config.addDefault(path + "RefreshingRain.ChargeTime",1000);
        config.addDefault(path + "RefreshingRain.Duration",5000);
        config.addDefault(path + "RefreshingRain.HealingDelay",1000);
        config.addDefault(path + "RefreshingRain.HealingPerDelay",1);
        config.addDefault(path + "RefreshingRain.Cooldown",12000);

        path = "Abilities.Plant.";

        config.addDefault(path + "SeedSummoner.MaxBarrierBeans",3);
        config.addDefault(path + "SeedSummoner.MaxPeaShots",2);
        config.addDefault(path + "SeedSummoner.MaxThornBarrages",2);
        config.addDefault(path + "SeedSummoner.MaxChiliBeans",2);

        config.addDefault(path + "SeedSummoner.ThornBarrage.Range",10);
        config.addDefault(path + "SeedSummoner.ThornBarrage.Speed",2);
        config.addDefault(path + "SeedSummoner.ThornBarrage.Damage",2);
        config.addDefault(path + "SeedSummoner.ThornBarrage.Hitbox",1);
        config.addDefault(path + "SeedSummoner.ThornBarrage.Cooldown",8000);
        config.addDefault(path + "SeedSummoner.ThornBarrage.Duration",8000);
        config.addDefault(path + "SeedSummoner.ThornBarrage.ShotDelay",1000);

        config.addDefault(path + "SeedSummoner.PeaShooter.Range",10);
        config.addDefault(path + "SeedSummoner.PeaShooter.Speed",2);
        config.addDefault(path + "SeedSummoner.PeaShooter.Damage",2);
        config.addDefault(path + "SeedSummoner.PeaShooter.Hitbox",1);
        config.addDefault(path + "SeedSummoner.PeaShooter.Cooldown",8000);
        config.addDefault(path + "SeedSummoner.PeaShooter.Duration",8000);
        config.addDefault(path + "SeedSummoner.PeaShooter.SelectRange",10);
        config.addDefault(path + "SeedSummoner.PeaShooter.PeaCount",10);

        config.addDefault(path + "SeedSummoner.ChiliBean.Amplitude", 0.5);
        config.addDefault(path + "SeedSummoner.ChiliBean.WaveLength", 3.0);
        config.addDefault(path + "SeedSummoner.ChiliBean.Period", 20.0);
        config.addDefault(path + "SeedSummoner.ChiliBean.ParticlesPerCycle", 20);
        config.addDefault(path + "SeedSummoner.ChiliBean.Range", 10);
        config.addDefault(path + "SeedSummoner.ChiliBean.Cooldown", 5000);
        config.addDefault(path + "SeedSummoner.ChiliBean.Damage", 2);
        config.addDefault(path + "SeedSummoner.ChiliBean.Hitbox", 1);
        config.addDefault(path + "SeedSummoner.ChiliBean.FireTicks", 40);


        config.addDefault(path + "SeedSummoner.BarrierBean.Duration", 5000);
        config.addDefault(path + "SeedSummoner.BarrierBean.Delay", 250);
        config.addDefault(path + "SeedSummoner.BarrierBean.MaxHeight", 0.5);
        config.addDefault(path + "SeedSummoner.BarrierBean.Radius", 3.0);
        config.addDefault(path + "SeedSummoner.BarrierBean.Cooldown", 2500);
        
        
        config.addDefault(path + "ThornyBush.SourceRange", 15);
        config.addDefault(path + "ThornyBush.Thorn.Thorns", 20);
        config.addDefault(path + "ThornyBush.Thorn.Amplitude", 3);
        config.addDefault(path + "ThornyBush.Thorn.Wavelength", 5);
        config.addDefault(path + "ThornyBush.Thorn.Period", 2);
        config.addDefault(path + "ThornyBush.Thorn.ParticlesPerCycle", 15);
        config.addDefault(path + "ThornyBush.Range", 15);
        config.addDefault(path + "ThornyBush.Thorn.Damage", 3);
        config.addDefault(path + "ThornyBush.Hitbox", .5);

        config.addDefault("Abilities.Plant.ThornyBush.Cooldown",7500);
        config.addDefault("Abilities.Plant.TendrilTwist.SelectRange",12);
        config.addDefault("Abilities.Plant.TendrilTwist.Damage",2);
        config.addDefault("Abilities.Plant.TendrilTwist.Hitbox",1);
        config.addDefault("Abilities.Plant.TendrilTwist.Range",25);
        config.addDefault("Abilities.Plant.TendrilTwist.SqueezeSpeed",.1);
        config.addDefault("Abilities.Plant.TendrilTwist.Squeezes",5);
        config.addDefault("Abilities.Plant.TendrilTwist.Speed",1);
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
