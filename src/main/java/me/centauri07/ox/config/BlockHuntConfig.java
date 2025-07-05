package me.centauri07.ox.config;

import me.centauri07.ox.event.EventReward;
import me.centauri07.ox.event.blockhunt.BlockHuntEventSettings;
import me.centauri07.ox.utility.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockHuntConfig extends Configuration {

    public BlockHuntConfig(JavaPlugin plugin) {
        super(plugin, "hidden_blocks");
    }

    public BlockHuntEventSettings settings;

    @Override
    protected void setup() {

        settings = new BlockHuntEventSettings(
                Location.getLocation(configuration.getConfigurationSection("locations.spawn")),
                Location.getLocation(configuration.getConfigurationSection("locations.return")),
                configuration.getInt("time-limit"),
                EventReward.fromConfig(configuration.getConfigurationSection("rewards"))
        );

    }

    @Override
    public void save() {
        // Save locations
        settings.huntLocation().writeToConfig(configuration.getConfigurationSection("locations.spawn"));
        settings.huntLocation().writeToConfig(configuration.getConfigurationSection("locations.return"));

        // Write to file
        try {
            configuration.save(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save hidden_blocks.yml", e);
        }
    }
}