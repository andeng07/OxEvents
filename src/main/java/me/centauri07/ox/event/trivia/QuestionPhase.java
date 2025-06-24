package me.centauri07.ox.event.trivia;

import me.centauri07.ox.config.MessagesConfiguration;
import me.centauri07.ox.event.EventPhase;
import me.centauri07.ox.utility.Countdown;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class QuestionPhase extends EventPhase {

    private Countdown countdown;

    private int questionIndex = -1;
    private final List<QuestionRound> questionRounds;

    public QuestionPhase(JavaPlugin plugin, TriviaEvent oxEvent, List<QuestionRound> questionRounds) {
        super(plugin, oxEvent);

        setListeners(List.of(new QuestionPhaseListener(this)));

        this.questionRounds = questionRounds;
    }

    @Override
    protected void start() {
        oxEvent.sendEventMessage(MessagesConfiguration.oxStartMessage);

        nextQuestion();
    }

    private void nextQuestion() {
        if (isLastQuestion()) {
            oxEvent.nextPhase();
            return;
        }

        questionIndex += 1;

        QuestionRound nextQuestion = questionRounds.get(questionIndex);

        oxEvent.sendEventMessage(nextQuestion.getPrompt());

        countdown = new Countdown(
                plugin, nextQuestion.getDuration(),
                count -> {
                    if (count % 10 == 0 && count < 5) {
                        String message = MessagesConfiguration.oxAnswerRevealCountdownMessage.replace("%seconds%", count + "");

                        oxEvent.sendEventMessage(message);
                    }
                },
                () -> {
                    String message = MessagesConfiguration.oxAnswerRevealMessage.replace("%answer%", nextQuestion.getAnswer());

                    oxEvent.sendEventMessage(message);

                    nextQuestion();
                }
        );
    }

    @Override
    protected void end() {
        countdown.cancel();
    }

    public QuestionRound getCurrentQuestionRound() {
        if (questionIndex < 0) return null;

        return questionRounds.get(questionIndex);
    }

    public boolean isLastQuestion() {

        return questionIndex >= questionRounds.size() - 1;

    }

}
