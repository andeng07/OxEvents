package me.centauri07.ox.event;

import me.centauri07.ox.config.MessagesConfiguration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class OxEvent {

    public static OxEvent currentEvent = null;

    public final Options options;

    private final List<EventPlayer> players = new ArrayList<>();

    private List<EventPhase> phases;

    protected EventPhase currentPhase = null;

    public OxEvent(Options options) {
        this.options = options;
    }

    public final void sendEventMessage(String message) {
        Component prefix = MiniMessage.miniMessage()
                .deserialize(MessagesConfiguration.prefix.replace("%event_name%", options.type().requestName));

        players.forEach(eventPlayer -> {

                    String messageParsed = message
                            .replace("%player_name%", eventPlayer.asPlayer().getName());

                    eventPlayer.asPlayer().sendMessage(prefix.append(MiniMessage.miniMessage().deserialize(messageParsed)));

                }
        );
    }

    public final boolean addPlayer(Player player) {
        if (!canJoin()) {
            String message = MessagesConfiguration.eventAlreadyStartedMessage.replace("%player%", player.getName());

            player.sendMessage(MiniMessage.miniMessage().deserialize(message));

            return false;
        }

        if (players.size() >= options.playerLimit()) {
            String message = MessagesConfiguration.eventFullMessage.replace("%player%", player.getName());

            player.sendMessage(MiniMessage.miniMessage().deserialize(message));

            return false;
        }

        if (isPlayer(player)) {
            String message = MessagesConfiguration.playerAlreadyInEventMessage.replace("%player%", player.getName());

            player.sendMessage(MiniMessage.miniMessage().deserialize(message));

            return false;
        }

        EventPlayer eventPlayer = new EventPlayer(player.getUniqueId(), EventPlayer.State.ALIVE);

        players.add(eventPlayer);

        sendEventMessage(MessagesConfiguration.eventJoinMessage);

        return true;
    }

    public final EventPlayer getEventPlayer(Player player) {
        return players.stream().filter(eventPlayer -> eventPlayer.getUniqueId() == player.getUniqueId())
                .findFirst().orElse(null);
    }


    public final EventPlayer getEventPlayer(UUID player) {
        return players.stream().filter(eventPlayer -> eventPlayer.getUniqueId() == player)
                .findFirst().orElse(null);
    }

    public final List<EventPlayer> getEventPlayers() {
        return players;
    }

    public final boolean isPlayer(Player player) {
        return players.stream().anyMatch(eventPlayer -> eventPlayer.getUniqueId() == player.getUniqueId());
    }

    public final EventPhase getCurrentPhase() {
        return currentPhase;
    }

    public final void nextPhase() {
        if (currentPhase != null) {
            currentPhase.terminate();
            int currentIndex = phases.indexOf(currentPhase);
            if (currentIndex >= 0 && currentIndex + 1 < phases.size()) {
                currentPhase = phases.get(currentIndex + 1);
            } else {
                terminate();
                return;
            }
        } else {
            currentPhase = phases.isEmpty() ? null : phases.getFirst();
        }

        if (currentPhase != null) {
            currentPhase.initialize();
        }
    }

    public final void terminate() {
        players.clear();
        phases = null;
        currentPhase = null;

        end();

        OxEvent.currentEvent = null;
    }

    public boolean canJoin() {
        return currentPhase instanceof WaitingPhase;
    }

    protected final void setEventPhases(List<EventPhase> eventPhases) {
        phases = List.copyOf(eventPhases);
    }

    protected final void setEventPhases(EventPhase... eventPhases) {
        if (phases == null) return;

        phases = Arrays.stream(eventPhases).toList();
    }

    public void start() { }
    public void end() { }

    public enum Type {
        TRIVIA("ox", "Ox Event"),
        PARKOUR("parkour", "Parkour Event"),
        BLOCK_HUNT("hidden_blocks", "Hidden Blocks Event");

        public final String id;
        public final String requestName;

        Type(String id, String requestName) {
            this.id = id;
            this.requestName = requestName;
        }
    }

}
