package me.centauri07.ox.event.blockhunt;

import me.centauri07.ox.utility.Location;
import org.bukkit.Material;

public record BlockData(Material material, Location location, String reward) {}
