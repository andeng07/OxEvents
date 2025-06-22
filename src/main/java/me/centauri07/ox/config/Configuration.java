package me.centauri07.ox.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public abstract class Configuration {

    private final JavaPlugin plugin;
    private final String fileName;

    protected YamlConfiguration configuration;

    public Configuration(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
    }

    public final void load() {
        File file = new File(plugin.getDataFolder(), fileName + ".yml");

        if (!file.exists()) {
            plugin.saveResource(fileName + ".yml", false);
        }

        configuration = new YamlConfiguration();

        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        setup();
    }

    protected abstract void setup();

}
