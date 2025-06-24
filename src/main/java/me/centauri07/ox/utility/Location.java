package me.centauri07.ox.utility;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

public record Location(String world, double x, double y, double z, float yaw, float pitch) {

    public static Location getLocation(ConfigurationSection section) {
        if (section == null) return null;

        String worldName = section.getString("world");
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        float yaw = (float) section.getDouble("yaw", 0);
        float pitch = (float) section.getDouble("pitch", 0);

        return new Location(worldName, x, y, z, yaw, pitch);
    }

    public org.bukkit.Location asBukkitLocation() {
        return new org.bukkit.Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

}
