package me.centauri07.ox.event;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class EventPhaseListener implements Listener {

    protected final JavaPlugin plugin;
    protected final OxEvent oxEvent;
    protected final EventPhase phase;

    protected EventPhaseListener(JavaPlugin plugin, OxEvent oxEvent, EventPhase phase) {
        this.plugin = plugin;
        this.oxEvent = oxEvent;
        this.phase = phase;
    }

}
