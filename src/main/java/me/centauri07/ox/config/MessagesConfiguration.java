package me.centauri07.ox.config;

import org.bukkit.plugin.java.JavaPlugin;

public class MessagesConfiguration extends Configuration {

    // Global prefix for all messages
    public static String prefix;

    // Event messages
    public static String eventStartCountdownMessage;
    public static String eventTimeRemainingMessage;
    public static String eventFullMessage;
    public static String eventAlreadyStartedMessage;
    public static String eventJoinMessage;
    public static String playerAlreadyInEventMessage;
    public static String playerEliminatedMessage;
    public static String playerWinMessage;

    // OX (Trivia) messages
    public static String oxStartMessage;
    public static String answerMessage;
    public static String alreadyAnsweredMessage;
    public static String eliminateMessage;

    // Trivia prompt formats
    public static String oxMultipleChoiceQuestionFormat;
    public static String oxMultipleChoiceOptionFormat;
    public static String oxTrueFalseQuestionFormat;
    public static String oxTrueFalseOptionFormat;
    public static String oxIdentificationQuestionFormat;

    // Trivia elimination and answer reveal
    public static String oxAlreadyEliminatedMessage;
    public static String oxAnswerRevealCountdownMessage;
    public static String oxAnswerRevealMessage;

    // Parkour messages
    public static String parkourStartMessage;
    public static String parkourCompletedMessage;
    public static String parkourAlreadyStarted;
    public static String parkourInvalidFinishAttempt;

    public MessagesConfiguration(JavaPlugin plugin) {
        super(plugin, "messages");
    }

    @Override
    protected void setup() {
        prefix = configuration.getString("prefix");

        // Event messages
        eventStartCountdownMessage = configuration.getString("event.start-countdown");
        eventTimeRemainingMessage = configuration.getString("event.time-remaining");
        eventFullMessage = configuration.getString("event.full");
        eventAlreadyStartedMessage = configuration.getString("event.already-started");
        eventJoinMessage = configuration.getString("event.join");
        playerAlreadyInEventMessage = configuration.getString("event.already-in-event");
        playerEliminatedMessage = configuration.getString("event.eliminated");
        playerWinMessage = configuration.getString("event.win");

        // OX (Trivia) messages
        oxStartMessage = configuration.getString("ox.start-message");
        answerMessage = configuration.getString("ox.answer");
        alreadyAnsweredMessage = configuration.getString("ox.already-answered");
        eliminateMessage = configuration.getString("ox.eliminate");

        // Trivia prompt formats
        oxMultipleChoiceQuestionFormat = configuration.getString("ox.prompt.multiple-choice.question-format");
        oxMultipleChoiceOptionFormat = configuration.getString("ox.prompt.multiple-choice.option-format");
        oxTrueFalseQuestionFormat = configuration.getString("ox.prompt.true-false.question-format");
        oxTrueFalseOptionFormat = configuration.getString("ox.prompt.true-false.option-format");
        oxIdentificationQuestionFormat = configuration.getString("ox.prompt.identification.question-format");

        // Trivia elimination and answer reveal
        oxAlreadyEliminatedMessage = configuration.getString("ox.already-eliminated");
        oxAnswerRevealCountdownMessage = configuration.getString("ox.answer-reveal.countdown");
        oxAnswerRevealMessage = configuration.getString("ox.answer-reveal.message");

        // Parkour messages
        parkourStartMessage = configuration.getString("parkour.start-message");
        parkourCompletedMessage = configuration.getString("parkour.completed");
        parkourAlreadyStarted = configuration.getString("parkour.already-started");
        parkourInvalidFinishAttempt = configuration.getString("parkour.invalid-finish-attempt");
    }
}
