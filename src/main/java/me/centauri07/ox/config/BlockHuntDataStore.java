package me.centauri07.ox.config;

import me.centauri07.ox.event.blockhunt.BlockData;
import me.centauri07.ox.utility.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BlockHuntDataStore extends Configuration {

    private final Map<String, BlockData> dataMap = new HashMap<>();

    public BlockHuntDataStore(JavaPlugin plugin) {
        super(plugin, "hidden_blocks_data");
    }

    @Override
    protected void setup() {
        loadDataFromConfig();
    }

    private void loadDataFromConfig() {
        dataMap.clear();

        for (String key : configuration.getKeys(false)) {
            ConfigurationSection section = configuration.getConfigurationSection(key);
            if (section == null) continue;

            Material material = Material.getMaterial("type");
            Location location = Location.getLocation(section);
            String reward = section.getString("reward");

            if (location != null && reward != null) {
                dataMap.put(key, new BlockData(material, location, reward));
            }
        }
    }

    public void add(BlockData data) {
        String key = locationKey(data.location());
        dataMap.put(key, data);

        configuration.set(key + ".world", data.location().world());
        configuration.set(key + ".x", data.location().x());
        configuration.set(key + ".y", data.location().y());
        configuration.set(key + ".z", data.location().z());
        configuration.set(key + ".yaw", data.location().yaw());
        configuration.set(key + ".pitch", data.location().pitch());
        configuration.set(key + ".reward", data.reward());

        save();
    }

    public void remove(org.bukkit.Location loc) {
        String key = locationKey(loc);
        dataMap.remove(key);
        configuration.set(key, null);
        save();
    }

    public Optional<BlockData> get(org.bukkit.Location loc) {
        return Optional.ofNullable(dataMap.get(locationKey(loc)));
    }

    public Collection<BlockData> getAll() {
        return dataMap.values();
    }

    private String locationKey(Location loc) {
        return loc.world() + ":" + (int) loc.x() + "," + (int) loc.y() + "," + (int) loc.z();
    }

    private String locationKey(org.bukkit.Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }


    @Override
    public void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save block_data.yml", e);
        }
    }
}
