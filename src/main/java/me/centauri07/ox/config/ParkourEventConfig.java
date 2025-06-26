package me.centauri07.ox.config;

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
                Location.getLocation(configuration.getConfigurationSection("parkour.location")),
                Location.getLocation(configuration.getConfigurationSection("parkour.return")),
                Location.getLocation(configuration.getConfigurationSection("parkour.start")),
                Location.getLocation(configuration.getConfigurationSection("parkour.end")),
                configuration.getInt("parkour.winner-count"),
                configuration.getInt("parkour.time-limit")
        );
    }
}
