package me.centauri07.ox.event.parkour;

import me.centauri07.ox.utility.Location;

public record ParkourEventSettings(Location parkourLocation, Location returnLocation, Location parkourStart, Location parkourEnd, int winnerCount, int timeLimit) { }
