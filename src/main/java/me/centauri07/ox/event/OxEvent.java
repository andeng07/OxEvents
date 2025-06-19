package me.centauri07.ox.event;

import me.centauri07.ox.config.MessagesConfiguration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class OxEvent {

    public final Options options;

    private final List<EventPlayer> players = new ArrayList<>();

    private final List<EventPhase> phases;

    protected EventPhase currentPhase = null;

    public OxEvent(Options options, List<EventPhase> phases) {
        this.options = options;
        this.phases = phases;
    }

    public final void sendEventMessage(String message) {
        Component prefix = MiniMessage.miniMessage()
                .deserialize(MessagesConfiguration.prefix.replace("%event_name%", options.type().requestName));

        players.forEach(eventPlayer -> {
                    Player retrievedPlayer = Bukkit.getPlayer(eventPlayer.getUniqueId());

                    String messageParsed = message // TODO: add all placeholder
                            .replace("%player_name%", eventPlayer.asPlayer().getName());

                    if (retrievedPlayer != null) {
                        retrievedPlayer.sendMessage(prefix.append(MiniMessage.miniMessage().deserialize(messageParsed)));
                    }
                }
        );
    }

    public final boolean addPlayer(Player player) {
        if (players.size() >= options.playerLimit()) return false;
        if (isPlayer(player)) return false;

        EventPlayer eventPlayer = new EventPlayer(player.getUniqueId(), EventPlayer.State.ALIVE);

        players.add(eventPlayer);

        return true;
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
                currentPhase = null; // or handle end-of-phases
                return;
            }
        } else {
            currentPhase = phases.isEmpty() ? null : phases.getFirst();
        }

        if (currentPhase != null) {
            currentPhase.initialize();
        }
    }

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
