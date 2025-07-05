package me.centauri07.ox.event.parkour;

import me.centauri07.ox.event.EventReward;
import me.centauri07.ox.utility.Location;

import java.util.List;

public record ParkourEventSettings(
        Location parkourLocation,
        Location returnLocation,
        Location parkourStart,
        Location parkourEnd,
        int winnerCount,
        int timeLimit,
        EventReward reward,
        List<String> blockedCommands
) { }
