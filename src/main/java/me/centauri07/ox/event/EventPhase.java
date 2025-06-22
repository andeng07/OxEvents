package me.centauri07.ox.event;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.EventListener;
import java.util.List;

public abstract class EventPhase implements EventListener {

    protected final JavaPlugin plugin;
    private List<EventPhaseListener> listeners = Collections.emptyList();
    protected final OxEvent oxEvent;

    public EventPhase(JavaPlugin plugin, OxEvent oxEvent) {
        this.plugin = plugin;
        this.oxEvent = oxEvent;
    }

    public final void initialize() {
        listeners.forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, plugin));

        start();
    }

    public final void terminate() {
        listeners.forEach(HandlerList::unregisterAll);

        end();
    }

    protected final void setListeners(List<EventPhaseListener> listeners) {
        if (!this.listeners.isEmpty()) return;

        this.listeners = listeners;
    }

    protected void start() { }

    protected void end() { }
}
