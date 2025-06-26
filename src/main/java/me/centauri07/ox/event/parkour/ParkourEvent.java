package me.centauri07.ox.event.parkour;

import me.centauri07.ox.event.Options;
import me.centauri07.ox.event.OxEvent;
import me.centauri07.ox.event.WaitingPhase;
import org.bukkit.plugin.java.JavaPlugin;

public class ParkourEvent extends OxEvent {

    private final JavaPlugin plugin;
    public final ParkourEventSettings settings;

    public ParkourEvent(JavaPlugin plugin, Options options, ParkourEventSettings settings) {
        super(options);

        this.plugin = plugin;
        this.settings = settings;

        setEventPhases(new WaitingPhase(plugin, this), new ParkourPhase(plugin, this, settings));
    }

}
