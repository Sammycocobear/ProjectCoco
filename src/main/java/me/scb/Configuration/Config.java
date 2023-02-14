package me.scb.Configuration;

import me.scb.ProjectCoco;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {
    private final Path path;
    private final FileConfiguration config;

    public Config(String name) {
        this.path = Paths.get(ProjectCoco.getPlugin().getDataFolder().toString(), name);
        this.config = YamlConfiguration.loadConfiguration(this.path.toFile());
        this.reloadConfig();
    }

    private void createConfig() {
        try {
            Files.createFile(this.path);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public void reloadConfig() {
        if (Files.notExists(this.path, new LinkOption[0])) {
            this.createConfig();
        }

        try {
            this.config.load(this.path.toFile());
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }


    public void save() {
        try {
            this.config.options().copyDefaults(true);
            this.config.save(this.path.toFile());
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }




}
