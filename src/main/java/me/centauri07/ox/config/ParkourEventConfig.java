package me.centauri07.ox.config;

import me.centauri07.ox.event.EventReward;
import me.centauri07.ox.event.parkour.ParkourEventSettings;
import me.centauri07.ox.utility.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class ParkourEventConfig extends Configuration {
    public ParkourEventConfig(JavaPlugin plugin) {
        super(plugin, "parkour-config");
    }

    public ParkourEventSettings parkourEventSettings;

    @Override
    protected void setup() {
        parkourEventSettings = new ParkourEventSettings(
                Location.getLocation(configuration.getConfigurationSection("locations.spawn")),
                Location.getLocation(configuration.getConfigurationSection("locations.return")),
                Location.getLocation(configuration.getConfigurationSection("locations.start")),
                Location.getLocation(configuration.getConfigurationSection("locations.end")),
                configuration.getInt("winner-count"),
                configuration.getInt("time-limit"),
                EventReward.fromConfig("reward", configuration.getConfigurationSection("reward")),
                configuration.getStringList("blocked-commands")
        );
    }

    @Override
    public void save() {
        parkourEventSettings.parkourLocation().writeToConfig(configuration.createSection("locations.spawn"));
        parkourEventSettings.returnLocation().writeToConfig(configuration.createSection("locations.return"));
        parkourEventSettings.parkourStart().writeToConfig(configuration.createSection("locations.start"));
        parkourEventSettings.parkourEnd().writeToConfig(configuration.createSection("locations.end"));

        // Persist to disk
        try {
            configuration.save(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save parkour-config.yml", e);
        }
    }
}
