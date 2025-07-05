package me.centauri07.ox.event.blockhunt;

import me.centauri07.ox.event.EventReward;
import me.centauri07.ox.utility.Location;

import java.util.List;

public record BlockHuntEventSettings(Location huntLocation, Location returnLocation, int timeLimit, List<EventReward> rewardList) {}
