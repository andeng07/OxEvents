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

    public static Location getLocation(org.bukkit.Location location) {
        return new Location(
                location.getWorld().getName(),
                location.x(), location.y(), location.z(),
                location.getYaw(), location.getPitch()
        );
    }

    public String asKey() {
        return world + ":" + x + "," + y + "," + z;
    }

    public org.bukkit.Location asBukkitLocation() {
        return new org.bukkit.Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public void writeToConfig(ConfigurationSection section) {
        section.set("world", world);
        section.set("x", x);
        section.set("y", y);
        section.set("z", z);
        section.set("yaw", yaw);
        section.set("pitch", pitch);
    }

}
