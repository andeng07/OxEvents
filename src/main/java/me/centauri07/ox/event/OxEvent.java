package me.centauri07.ox.event;

import me.centauri07.ox.config.MessagesConfiguration;
import me.centauri07.ox.utility.Countdown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class OxEvent {

    private final JavaPlugin plugin;

    public static OxEvent currentEvent = null;

    public final Options options;

    private final List<EventPlayer> players = new ArrayList<>();

    private List<EventPhase> phases;

    protected EventPhase currentPhase = null;

    public OxEvent(JavaPlugin plugin, Options options) {
        this.plugin = plugin;
        this.options = options;

        start();
    }

    public final void sendEventMessage(String message) {
        Component prefix = MiniMessage.miniMessage()
                .deserialize(MessagesConfiguration.prefix.replace("%event%", options.type().requestName));

        players.forEach(eventPlayer -> {
                    Player player = eventPlayer.asPlayer();

                    if (player != null) {
                        String messageParsed = message
                                .replace("%player_name%", player.getName());

                        player.sendMessage(prefix.append(MiniMessage.miniMessage().deserialize(messageParsed)));
                    }
                }
        );
    }

    public final void sendEventTitle(String message) {
        Component prefix = MiniMessage.miniMessage()
                .deserialize(MessagesConfiguration.prefix.replace("%event%", options.type().requestName));

        players.forEach(eventPlayer -> {
                    Player player = eventPlayer.asPlayer();

                    if (player != null) {
                        String messageParsed = message
                                .replace("%player_name%", player.getName());


                        player.showTitle(Title.title(prefix.append(MiniMessage.miniMessage().deserialize(messageParsed)), Component.empty()));
                    }
                }
        );
    }

    public final void sendActionBar(String message) {

        Component prefix = MiniMessage.miniMessage()
                .deserialize(MessagesConfiguration.prefix.replace("%event%", options.type().requestName));

        players.forEach(eventPlayer -> {
                    Player player = eventPlayer.asPlayer();

                    if (player != null) {
                        String messageParsed = message
                                .replace("%player_name%", player.getName());

                        player.sendActionBar(prefix.append(MiniMessage.miniMessage().deserialize(messageParsed)));
                    }
                }
        );
    }


    public final boolean addPlayer(Player player, EventPlayer.State state) {
        if (!canJoin()) {
            String message = MessagesConfiguration.eventAlreadyStartedMessage.replace("%player%", player.getName());

            player.sendMessage(MiniMessage.miniMessage().deserialize(message));

            return false;
        }

        if (getEventPlayers(eventPlayer -> !eventPlayer.asPlayer().hasPermission("oxevent.admin")).size() >= options.playerLimit()) {
            String message = MessagesConfiguration.eventFullMessage.replace("%player%", player.getName());

            player.sendMessage(MiniMessage.miniMessage().deserialize(message));

            return false;
        }

        if (isPlayer(player)) {
            String message = MessagesConfiguration.playerAlreadyInEventMessage.replace("%player%", player.getName());

            player.sendMessage(MiniMessage.miniMessage().deserialize(message));

            return false;
        }

        EventPlayer eventPlayer = new EventPlayer(player.getUniqueId(), player.hasPermission("oxevent.admin") ? EventPlayer.State.ELIMINATED : EventPlayer.State.ALIVE);

        players.add(eventPlayer);

        player.sendMessage(
                MessagesConfiguration.eventJoinMessage
        );

        return true;
    }

    public void removePlayer(Player player) {
        if (!isPlayer(player)) return;

        players.removeIf(eventPlayer -> eventPlayer.getUniqueId() == player.getUniqueId());
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

    public final List<EventPlayer> getEventPlayers(Predicate<EventPlayer> predicate) {
        return players.stream().filter(predicate).toList();
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
                new Countdown(plugin, 5, tick ->
                        sendActionBar(MessagesConfiguration.eventTerminateCountdown.replace("%time_remaining%", String.valueOf(tick))
                        ), this::terminate).start();
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
        end();

        players.clear();
        phases = null;
        currentPhase = null;

        OxEvent.currentEvent = null;
    }

    public final void forceStop() {
        if (currentPhase != null && currentPhase != phases.getLast()) {
            currentPhase.terminate();

            currentPhase = phases.getLast();
        }

        nextPhase();
    }

    public boolean canJoin() {
        return currentPhase instanceof WaitingPhase;
    }

    protected final void setEventPhases(List<EventPhase> eventPhases) {
        phases = List.copyOf(eventPhases);
    }

    protected final void setEventPhases(EventPhase... eventPhases) {
        phases = Arrays.stream(eventPhases).toList();
    }

    public void start() {
    }

    public void end() {
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
