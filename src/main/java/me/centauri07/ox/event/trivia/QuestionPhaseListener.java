package me.centauri07.ox.event.trivia;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.centauri07.ox.config.MessagesConfiguration;
import me.centauri07.ox.event.EventPhaseListener;
import me.centauri07.ox.event.EventPlayer;
import me.centauri07.ox.event.OxEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;

public class QuestionPhaseListener extends EventPhaseListener {

    private final QuestionPhase phase;

    protected QuestionPhaseListener(QuestionPhase phase) {
        this.phase = phase;
    }

    @EventHandler
    public void onMessage(AsyncChatEvent event) {

        if (!(OxEvent.currentEvent instanceof TriviaEvent triviaEvent)) return;

        EventPlayer eventPlayer = triviaEvent.getEventPlayer(event.getPlayer());

        if (eventPlayer == null) return;

        if (!(triviaEvent.getCurrentPhase() instanceof QuestionPhase questionPhase)) return;

        event.setCancelled(true);

        if (questionPhase.getCurrentQuestionRound().isParticipant(eventPlayer)) {
            if (questionPhase.getCurrentQuestionRound().hasAnswered(eventPlayer)) {
                eventPlayer.asPlayer().sendMessage(MiniMessage.miniMessage().deserialize(MessagesConfiguration.alreadyAnsweredMessage));

                return;
            }

            String message = MiniMessage.miniMessage().serialize(event.originalMessage());

            event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(MessagesConfiguration.answerMessage.replace("%selected_answer%", message)));

            boolean answerResult = phase.getCurrentQuestionRound()
                    .answer(eventPlayer, MiniMessage.miniMessage().serialize(event.originalMessage()));

            if (phase.isLastQuestion() && answerResult) {
                phase.setWinner(eventPlayer);
                triviaEvent.nextPhase(); // end
            }

        } else {
            String message = MessagesConfiguration.oxAlreadyEliminatedMessage;

            eventPlayer.asPlayer().sendMessage(MiniMessage.miniMessage().deserialize(message));
        }

    }
}
