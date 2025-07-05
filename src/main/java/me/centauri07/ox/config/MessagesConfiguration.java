package me.centauri07.ox.config;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Configuration class for loading all messages used by the event system.
 * Messages are loaded from the "messages.yml" file.
 *
 * Messages include event countdowns, player notifications, trivia prompts,
 * answer reveal messages, and parkour event messages.
 */
public class MessagesConfiguration extends Configuration {

    // Prefix applied to all messages sent by the event system (minimal, elegant)
    public static String prefix;

    // Event messages
    public static String waitingActionBar;

    /** Countdown before the event officially begins (%seconds%) */
    public static String eventStartCountdownMessage;

    /** Periodic message showing how much time is left in the event (%time%) */
    public static String eventTimeRemainingMessage;

    /** Shown when event participant limit is reached */
    public static String eventFullMessage;

    /** Displayed if event already started and player tries to join */
    public static String eventAlreadyStartedMessage;

    /** Confirmation message when player joins event successfully */
    public static String eventJoinMessage;

    /** Shown if player tries to join but is already in the event */
    public static String playerAlreadyInEventMessage;

    /** Message when a player is eliminated from the event */
    public static String playerEliminatedMessage;

    /** Victory message when one or more players win (%winners%) */
    public static String playerWinMessage;

    public static String eventTerminateCountdown;

    public static String eventLeaveMessage;

    // Trivia (OX) messages
    /** Introduction message when Trivia starts */
    public static String oxInitializeMessage;
    public static String oxStartMessage;

    /** Feedback when player submits an answer (%answer%) */
    public static String answerMessage;

    /** Message if player tries to answer again in the same round */
    public static String alreadyAnsweredMessage;

    /** Elimination message specific to Trivia/OX */
    public static String eliminateMessage;

    // Trivia prompt formats
    /** Multiple choice question prompt (%question%) */
    public static String oxMultipleChoiceQuestionFormat;

    /** Multiple choice option format (%option_letter%, %option_text%) */
    public static String oxMultipleChoiceOptionFormat;

    /** True/False question prompt (%question%) */
    public static String oxTrueFalseQuestionFormat;

    /** True/False option format (%option_text%) */
    public static String oxTrueFalseOptionFormat;

    /** Identification (open-ended) question prompt (%question%) */
    public static String oxIdentificationQuestionFormat;

    // Trivia elimination and answer reveal
    /** Message if eliminated player tries to answer again */
    public static String oxAlreadyEliminatedMessage;

    /** Countdown before revealing the correct answer (%seconds%) */
    public static String oxAnswerRevealCountdownMessage;

    /** Summary message after answer reveal (%answer%, %correct%, %wrong%, %alive%) */
    public static String oxAnswerRevealMessage;

    public static String oxEndMessage;

    // Parkour messages
    public static String parkourInitializeMessage;
    /** Introduction message when Parkour starts */
    public static String parkourStartMessage;

    /** Message when player starts parkour course */
    public static String parkourStartCourseMessage;

    /** Message when player completes the course (%player%, %current%, %required%) */
    public static String parkourCompletedMessage;

    /** Warning when player tries to start again */
    public static String parkourAlreadyStarted;

    /** Message when player tries to finish without starting */
    public static String parkourInvalidFinishAttempt;

    /** Message shown when the event ends and players are being returned to spawn */
    public static String parkourReturnCountdownMessage;

    public static String parkourEndMessage;

    // Block hunt (hidden blocks) messages
    /** */
    public static String hiddenBlocksInitializeMessage;
    public static String hiddenBlocksStartMessage;
    public static String hiddenBlocksFoundReward;

    public static String hiddenBlocksEndMessage;

    public MessagesConfiguration(JavaPlugin plugin) {
        super(plugin, "messages");
    }

    @Override
    protected void setup() {
        prefix = configuration.getString("prefix");

        // Event messages
        waitingActionBar = configuration.getString("waiting-action-bar");
        eventStartCountdownMessage = configuration.getString("event.start-countdown");
        eventTimeRemainingMessage = configuration.getString("event.time-remaining");
        eventFullMessage = configuration.getString("event.full");
        eventAlreadyStartedMessage = configuration.getString("event.already-started");
        eventJoinMessage = configuration.getString("event.join");
        playerAlreadyInEventMessage = configuration.getString("event.already-in");
        playerEliminatedMessage = configuration.getString("event.eliminated");
        playerWinMessage = configuration.getString("event.win");
        eventTerminateCountdown = configuration.getString("event.terminate-countdown");
        eventLeaveMessage = configuration.getString("event.leave");

        // OX (Trivia) messages
        oxInitializeMessage = configuration.getString("ox.initialize-message");
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

        oxEndMessage = configuration.getString("ox.end-message");

        // Parkour messages
        parkourInitializeMessage = configuration.getString("parkour.initialize-message");
        parkourStartMessage = configuration.getString("parkour.start-message");
        parkourStartCourseMessage = configuration.getString("parkour.start-course");
        parkourCompletedMessage = configuration.getString("parkour.completed");
        parkourAlreadyStarted = configuration.getString("parkour.already-started");
        parkourInvalidFinishAttempt = configuration.getString("parkour.invalid-finish-attempt");
        parkourReturnCountdownMessage = configuration.getString("parkour.return-countdown");

        parkourEndMessage = configuration.getString("parkour.end-message");

        // Hidden blocks
        hiddenBlocksInitializeMessage = configuration.getString("hidden-blocks.initialize-message");
        hiddenBlocksStartMessage = configuration.getString("hidden-blocks.start-message");
        hiddenBlocksFoundReward = configuration.getString("hidden-blocks.found-reward");
        parkourReturnCountdownMessage = configuration.getString("hidden-blocks.return-countdown");

        hiddenBlocksEndMessage = configuration.getString("hidden-blocks.end-message");
    }
}
