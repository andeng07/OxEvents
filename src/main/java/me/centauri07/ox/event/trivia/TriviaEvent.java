package me.centauri07.ox.event.trivia;

import me.centauri07.ox.config.MessagesConfiguration;
import me.centauri07.ox.event.EventPlayer;
import me.centauri07.ox.event.Options;
import me.centauri07.ox.event.OxEvent;
import me.centauri07.ox.event.WaitingPhase;
import org.bukkit.plugin.java.JavaPlugin;

public class TriviaEvent extends OxEvent {

    private EventPlayer winner;

    public TriviaEvent(JavaPlugin plugin, Options options, TriviaEventSettings settings) {
        super(options);

        setEventPhases(
                new WaitingPhase(plugin, this),
                new QuestionPhase(plugin, this, settings.questions())
        );
    }

    public void setWinner(EventPlayer eventPlayer) {
        if (winner != null) throw new IllegalStateException("Events > Winner already exist.");

        winner = eventPlayer;
    }

    private void rewardWinner(EventPlayer eventPlayer) {
        // settings.reward().apply(eventPlayer.asPlayer());
    }

    @Override
    public void end() {
        if (winner != null) {
            rewardWinner(winner);
        }

        String winnerMessage = MessagesConfiguration.playerWinMessage.replace("%winners%", winner != null ? winner.asPlayer().getName() : "No one");

        sendEventMessage(winnerMessage);

    }
}
