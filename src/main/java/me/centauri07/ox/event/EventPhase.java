package me.centauri07.ox.event;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.EventListener;
import java.util.List;

public abstract class EventPhase implements EventListener {

    private final JavaPlugin plugin;
    private final OxEvent oxEvent;
    private final List<EventPhaseListener> listeners;

    public EventPhase(JavaPlugin plugin, OxEvent oxEvent, List<EventPhaseListener> listeners) {
        this.plugin = plugin;
        this.oxEvent = oxEvent;
        this.listeners = listeners;
    }

    public EventPhase(JavaPlugin plugin, OxEvent oxEvent) {
        this.plugin = plugin;
        this.oxEvent = oxEvent;
        this.listeners = Collections.emptyList();
    }

    public final void initialize() {
        listeners.forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, plugin));

        start();
    }

    public final void terminate() {
        listeners.forEach(HandlerList::unregisterAll);

        end();
    }

    protected void start() { }

    protected void end() { }
}
