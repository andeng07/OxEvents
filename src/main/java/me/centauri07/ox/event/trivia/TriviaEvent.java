package me.centauri07.ox.event.trivia;

import me.centauri07.ox.config.MessagesConfiguration;
import me.centauri07.ox.event.EventPlayer;
import me.centauri07.ox.event.Options;
import me.centauri07.ox.event.OxEvent;
import me.centauri07.ox.event.WaitingPhase;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class TriviaEvent extends OxEvent {

    private EventPlayer winner;
    private final TriviaEventSettings settings;

    public TriviaEvent(JavaPlugin plugin, Options options, TriviaEventSettings settings) {
        super(options);

        this.settings = settings;

        setEventPhases(List.of(
                new WaitingPhase(plugin, this),
                new QuestionPhase(plugin, this, List.copyOf(settings.questionRounds()))

        ));
    }

    public EventPlayer getWinner() {
        if (winner != null) return winner;

        List<EventPlayer> alivePlayers = getEventPlayers().stream()
                .filter(eventPlayer -> eventPlayer.getState() == EventPlayer.State.ALIVE).toList();

        if (alivePlayers.size() != 1) return null;

        return alivePlayers.getFirst();
    }

    public void setWinner(EventPlayer eventPlayer) {
        if (winner != null) throw new IllegalStateException("Events > Winner already exist.");

        winner = eventPlayer;
    }

    private void rewardWinner(EventPlayer eventPlayer) {
        settings.reward().apply(eventPlayer.asPlayer());
    }

    @Override
    public void end() {
        EventPlayer finalWinner = getWinner();

        if (finalWinner == null ) {
            throw new IllegalStateException("Events > Event ends with no winners");
        }

        rewardWinner(finalWinner);

        String winnerMessage = MessagesConfiguration.playerWinMessage.replace("%winners%", finalWinner.asPlayer().getName());

        sendEventMessage(winnerMessage);

    }
}
