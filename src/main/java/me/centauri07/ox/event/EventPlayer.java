package me.centauri07.ox.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EventPlayer {

    private State state;

    private final UUID uniqueId;

    public EventPlayer(UUID uniqueId, State state) {
        this.uniqueId = uniqueId;
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public Player asPlayer() {
        return Bukkit.getPlayer(uniqueId);
    }

    public enum State {
        ALIVE, ELIMINATED
    }

}
