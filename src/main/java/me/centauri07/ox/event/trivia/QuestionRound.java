package me.centauri07.ox.event.trivia;

import me.centauri07.ox.config.MessagesConfiguration;
import me.centauri07.ox.event.EventPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class QuestionRound {

    public static List<QuestionRound> fromConfig(YamlConfiguration config) {
        List<QuestionRound> rounds = new ArrayList<>();

        ConfigurationSection questionsSection = config.getConfigurationSection("questions");
        if (questionsSection == null) {
            throw new IllegalArgumentException("No 'questions' section found in config");
        }

        Set<String> keys = questionsSection.getKeys(false);

        for (String key : keys) {
            ConfigurationSection section = questionsSection.getConfigurationSection(key);
            if (section == null) {
                throw new IllegalArgumentException("Invalid question entry for key: " + key);
            }
            rounds.add(parseQuestionSection(section));
        }

        return rounds;
    }

    private static QuestionRound parseQuestionSection(ConfigurationSection section) {
        String question = section.getString("question");
        if (question == null || question.isEmpty()) {
            throw new IllegalArgumentException("Question text missing");
        }

        String typeStr = section.getString("type");
        if (typeStr == null) {
            throw new IllegalArgumentException("Question type missing for question: " + question);
        }

        Type type;
        try {
            type = Type.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid question type '" + typeStr + "' for question: " + question);
        }

        int duration = section.getInt("duration", 30); // default 30 sec

        List<String> choices = null;
        if (type == Type.MULTIPLE_CHOICE) {
            choices = section.getStringList("choices");
            if (choices.isEmpty()) {
                throw new IllegalArgumentException("Choices required for multiple choice question: " + question);
            }
        } else if (type == Type.TRUE_OR_FALSE) {
            // Implicit choices for true/false questions
            choices = List.of("True", "False");
        }

        String answer = section.getString("answer");
        if (answer == null || answer.isEmpty()) {
            throw new IllegalArgumentException("Answer missing for question: " + question);
        }

        return new QuestionRound(question, choices, answer, duration, type);
    }

    private final String question;
    private final List<String> choices;
    private final String answer; // Always a string: letter for multiple choice, text for others
    private final int duration;
    private final Type type;

    private final Map<UUID, Boolean> playersWhoAnswered = new HashMap<>();

    // Constructor for all types, answer is always string
    public QuestionRound(String question, List<String> choices, String answer, int duration, Type type) {
        this.question = question;
        this.choices = choices;
        this.answer = answer;
        this.duration = duration;
        this.type = type;

        validate();
    }

    private void validate() {
        switch (type) {
            case MULTIPLE_CHOICE -> {
                if (choices == null || choices.size() < 2) {
                    throw new IllegalArgumentException("Multiple choice questions must have at least 2 options.");
                }
                if (answer == null || answer.length() != 1) {
                    throw new IllegalArgumentException("Answer for multiple choice must be a single letter (e.g., 'A').");
                }
                char answerChar = Character.toUpperCase(answer.charAt(0));
                int index = answerChar - 'A';
                if (index < 0 || index >= choices.size()) {
                    throw new IllegalArgumentException("Answer letter '" + answer + "' is out of choices range.");
                }
            }

            case TRUE_OR_FALSE -> {
                if (!answer.equalsIgnoreCase("True") && !answer.equalsIgnoreCase("False")) {
                    throw new IllegalArgumentException("True/False answers must be either 'True' or 'False'.");
                }
            }

            case IDENTIFICATION -> {
                if (choices != null && !choices.isEmpty()) {
                    throw new IllegalArgumentException("Identification questions must not have predefined choices.");
                }
                if (answer == null || answer.trim().isEmpty()) {
                    throw new IllegalArgumentException("Identification questions must have a non-empty answer.");
                }
            }
        }
    }

    /**
     * Checks player's answer.
     * Player inputs letter (A, B, C...) for multiple choice,
     * and text for true/false or identification.
     */
    public boolean answer(EventPlayer eventPlayer, String playerAnswer) {
        if (playersWhoAnswered.containsKey(eventPlayer.getUniqueId())) {
            return playersWhoAnswered.get(eventPlayer.getUniqueId());
        }

        boolean isCorrect = false;

        switch (type) {
            case MULTIPLE_CHOICE -> {
                if (playerAnswer == null || playerAnswer.length() != 1) {
                    isCorrect = false;
                    break;
                }
                char inputChar = Character.toUpperCase(playerAnswer.charAt(0));
                isCorrect = (inputChar == Character.toUpperCase(answer.charAt(0)));
            }
            case TRUE_OR_FALSE, IDENTIFICATION -> {
                isCorrect = answer.equalsIgnoreCase(playerAnswer.trim());
            }
        }

        playersWhoAnswered.put(eventPlayer.getUniqueId(), isCorrect);

        if (!isCorrect) {
            eventPlayer.setState(EventPlayer.State.ELIMINATED);
        }

        return isCorrect;
    }

    /**
     * Builds the prompt to show players.
     * Shows letters A), B), C) for multiple choice.
     */
    public String getPrompt() {
        StringBuilder builder = new StringBuilder();

        switch (type) {
            case MULTIPLE_CHOICE -> {
                String questionFormat = MessagesConfiguration.oxMultipleChoiceQuestionFormat;
                String optionFormat = MessagesConfiguration.oxMultipleChoiceOptionFormat;

                builder.append(questionFormat.replace("%question%", question)).append("\n");

                char letter = 'A';
                for (String choice : choices) {
                    builder.append(optionFormat
                                    .replace("%option_letter%", String.valueOf(letter))
                                    .replace("%option_text%", choice))
                            .append("\n");
                    letter++;
                }
            }

            case TRUE_OR_FALSE -> {
                String questionFormat = MessagesConfiguration.oxTrueFalseQuestionFormat;
                String optionFormat = MessagesConfiguration.oxTrueFalseOptionFormat;

                builder.append(questionFormat.replace("%question%", question)).append("\n");

                builder.append(optionFormat
                                .replace("%option_letter%", "A")
                                .replace("%option_text%", "True"))
                        .append("\n");

                builder.append(optionFormat
                                .replace("%option_letter%", "B")
                                .replace("%option_text%", "False"))
                        .append("\n");
            }

            case IDENTIFICATION -> {
                String questionFormat = MessagesConfiguration.oxIdentificationQuestionFormat;
                builder.append(questionFormat.replace("%question%", question));
            }
        }

        return builder.toString().trim();
    }

    // Getters

    public String getQuestion() {
        return question;
    }

    public List<String> getChoices() {
        return choices;
    }

    public int getDuration() {
        return duration;
    }

    public String getAnswer() {
        if (answer == null) return null;

        switch (type) {
            case MULTIPLE_CHOICE -> {
                char letter = Character.toUpperCase(answer.charAt(0));
                int index = letter - 'A';
                if (choices != null && index >= 0 && index < choices.size()) {
                    return choices.get(index);
                } else {
                    return null;
                }
            }
            case TRUE_OR_FALSE, IDENTIFICATION -> {
                return answer.trim();
            }
        }

        return null;
    }


    public Type getType() {
        return type;
    }

    public enum Type {
        MULTIPLE_CHOICE,
        TRUE_OR_FALSE,
        IDENTIFICATION
    }
}
