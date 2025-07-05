package me.centauri07.ox.event.trivia;

import me.centauri07.ox.config.MessagesConfiguration;
import me.centauri07.ox.event.EventPhase;
import me.centauri07.ox.event.EventPlayer;
import me.centauri07.ox.utility.Countdown;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuestionPhase extends EventPhase {

    private EventPlayer winner;

    private final TriviaEvent triviaEvent;
    private final TriviaEventSettings settings;
    private final List<Question> questions;

    private Countdown countdown;
    private int questionIndex = -1;
    private QuestionRound currentQuestion;

    public QuestionPhase(JavaPlugin plugin, TriviaEvent oxEvent, TriviaEventSettings settings) {
        super(plugin, oxEvent);
        this.triviaEvent = oxEvent;
        this.settings = settings;
        this.questions = settings.questions();

        setListeners(List.of(new QuestionPhaseListener(this)));
    }

    @Override
    protected void start() {
        oxEvent.sendEventMessage(MessagesConfiguration.oxStartMessage);
        nextQuestion();
    }

    @Override
    protected void end() {
        if (countdown != null && countdown.isCancelled()) {
            countdown.cancel();
        }

        if (winner != null) {
            rewardWinner(winner);
        }

        oxEvent.sendEventMessage(MessagesConfiguration.oxEndMessage);

        String winnerMessage = MessagesConfiguration.playerWinMessage.replace("%winners%", winner != null ? winner.asPlayer().getName() : "No one");

        oxEvent.sendEventMessage(winnerMessage);
    }

    public QuestionRound getCurrentQuestionRound() {
        return currentQuestion;
    }

    public boolean isLastQuestion() {
        return questionIndex >= questions.size() - 1;
    }

    private void nextQuestion() {
        // Step 1: Eliminate unresponsive players from the previous round
        if (currentQuestion != null) {
            eliminatePlayers();
        }

        // Step 2: Check game end conditions
        List<EventPlayer> alivePlayers = oxEvent.getEventPlayers().stream()
                .filter(player -> player.getState() == EventPlayer.State.ALIVE)
                .toList();

        if (isLastQuestion() || alivePlayers.size() <= 1) {
            setWinner(alivePlayers.isEmpty() ? null : alivePlayers.getFirst());
            oxEvent.nextPhase();
            return;
        }

        // Step 3: Start the next round
        questionIndex++;
        currentQuestion = new QuestionRound(questions.get(questionIndex), alivePlayers);

        oxEvent.sendEventMessage(currentQuestion.getPrompt());

        countdown = new Countdown(
                plugin,
                currentQuestion.getQuestion().duration(),
                count -> {
                    String message = MessagesConfiguration.oxAnswerRevealCountdownMessage
                            .replace("%time_remaining%", String.valueOf(count));
                    oxEvent.sendActionBar(message);
                },
                this::onCountdownFinish
        );

        countdown.start();
    }

    private void eliminatePlayers() {
        for (Map.Entry<UUID, Boolean> entry : currentQuestion.getParticipants().entrySet()) {
            EventPlayer eventPlayer = oxEvent.getEventPlayer(entry.getKey());

            if (eventPlayer == null) continue;

            if (!currentQuestion.hasAnswered(eventPlayer)) {
                eventPlayer.setState(EventPlayer.State.ELIMINATED);
            }

            if (eventPlayer.getState() == EventPlayer.State.ELIMINATED) {
                Player player = eventPlayer.asPlayer();

                if (player == null) return;

                player.sendMessage(MiniMessage.miniMessage().deserialize(MessagesConfiguration.eliminateMessage));
            }
        }
    }

    private void onCountdownFinish() {
        long correctCount = currentQuestion.getParticipants().values().stream().filter(b -> b).count();
        long wrongCount = currentQuestion.getParticipants().values().stream().filter(b -> !b).count();
        long remainingCount = oxEvent.getEventPlayers().stream()
                .filter(player -> player.getState() == EventPlayer.State.ALIVE).count();

        String message = MessagesConfiguration.oxAnswerRevealMessage
                .replace("%correct_answer%", currentQuestion.getQuestion().answer())
                .replace("%correct_count%", String.valueOf(correctCount))
                .replace("%wrong_count%", String.valueOf(wrongCount))
                .replace("%remaining_players%", String.valueOf(remainingCount));

        oxEvent.sendEventMessage(message);

        nextQuestion(); // Go to next round
    }

    public void setWinner(EventPlayer eventPlayer) {
        if (winner != null) throw new IllegalStateException("Events > Winner already exist.");

        winner = eventPlayer;
    }

    private void rewardWinner(EventPlayer eventPlayer) {
        Player player = eventPlayer.asPlayer();

        if (player == null) return;

        settings.reward().apply(player);
    }

}
