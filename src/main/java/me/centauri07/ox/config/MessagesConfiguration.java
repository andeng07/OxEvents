package me.centauri07.ox.config;

import org.bukkit.plugin.java.JavaPlugin;

public class MessagesConfiguration extends Configuration {

    // Global prefix for all messages
    public static String prefix;

    // Event messages
    public static String eventStartCountdownMessage;
    public static String eventFullMessage;
    public static String eventAlreadyStartedMessage;
    public static String eventJoinMessage;
    public static String eventStartMessage;
    public static String playerAlreadyInEventMessage;
    public static String playerEliminatedMessage;
    public static String playerWinMessage;

    // Trivia (OX) prompt formats
    public static String oxMultipleChoiceQuestionFormat;
    public static String oxMultipleChoiceOptionFormat;

    public static String oxTrueFalseQuestionFormat;
    public static String oxTrueFalseOptionFormat;

    public static String oxIdentificationQuestionFormat;

    // Trivia elimination message
    public static String oxAlreadyEliminatedMessage;

    // Trivia answer reveal messages
    public static String oxAnswerRevealCountdownMessage;
    public static String oxAnswerRevealMessage;

    public MessagesConfiguration(JavaPlugin plugin) {
        super(plugin, "messages");
    }

    @Override
    protected void setup() {
        prefix = configuration.getString("prefix");

        // Event messages
        eventStartCountdownMessage = configuration.getString("event.start-countdown");
        eventFullMessage = configuration.getString("event.full");
        eventAlreadyStartedMessage = configuration.getString("event.already-started");
        eventJoinMessage = configuration.getString("event.join");
        eventStartMessage = configuration.getString("event.start");
        playerAlreadyInEventMessage = configuration.getString("event.already-in-event");
        playerEliminatedMessage = configuration.getString("event.eliminated");
        playerWinMessage = configuration.getString("event.win");

        // OX prompt formats - multiple choice
        oxMultipleChoiceQuestionFormat = configuration.getString("ox.prompt.multiple-choice.question-format");
        oxMultipleChoiceOptionFormat = configuration.getString("ox.prompt.multiple-choice.option-format");

        // OX prompt formats - true/false
        oxTrueFalseQuestionFormat = configuration.getString("ox.prompt.true-false.question-format");
        oxTrueFalseOptionFormat = configuration.getString("ox.prompt.true-false.option-format");

        // OX prompt format - identification (open-ended)
        oxIdentificationQuestionFormat = configuration.getString("ox.prompt.identification.question-format");

        // OX elimination message
        oxAlreadyEliminatedMessage = configuration.getString("ox.already-eliminated");

        // OX answer reveal messages
        oxAnswerRevealCountdownMessage = configuration.getString("ox.answer-reveal.countdown");
        oxAnswerRevealMessage = configuration.getString("ox.answer-reveal.message");
    }
}