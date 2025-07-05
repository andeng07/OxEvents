package me.centauri07.ox.event.trivia;

import me.centauri07.ox.config.MessagesConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public record Question(String question, List<String> choices, String answer, int duration,
                       me.centauri07.ox.event.trivia.Question.Type type) {

    public static Question fromConfigSection(ConfigurationSection section) {
        String question = section.getString("question");
        String typeStr = section.getString("type");
        String answer = section.getString("answer");
        int duration = section.getInt("duration", 30);

        if (question == null || typeStr == null || answer == null) {
            throw new IllegalArgumentException("Missing required fields in question block.");
        }

        Type type = Type.valueOf(typeStr.toUpperCase());

        List<String> choices = null;
        if (type == Type.MULTIPLE_CHOICE) {
            choices = section.getStringList("choices");
            if (choices.isEmpty()) {
                throw new IllegalArgumentException("Choices are required for multiple choice.");
            }
        } else if (type == Type.TRUE_OR_FALSE) {
            choices = List.of("True", "False");
        }

        return new Question(question, choices, answer, duration, type);
    }

    public static List<Question> fromConfigList(YamlConfiguration config) {
        ConfigurationSection questionsSection = config.getConfigurationSection("questions");
        if (questionsSection == null) {
            throw new IllegalArgumentException("No 'questions' section found in config");
        }

        List<Question> questions = new ArrayList<>();

        for (String key : questionsSection.getKeys(false)) {
            ConfigurationSection section = questionsSection.getConfigurationSection(key);
            if (section == null) {
                throw new IllegalArgumentException("Invalid question entry: " + key);
            }

            questions.add(fromConfigSection(section));
        }

        return questions;
    }

    public Question(String question, List<String> choices, String answer, int duration, Type type) {
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

    public boolean isCorrect(String playerAnswer) {
        return switch (type) {
            case MULTIPLE_CHOICE -> {
                if (playerAnswer == null || playerAnswer.length() != 1) yield false;
                char inputChar = Character.toUpperCase(playerAnswer.charAt(0));
                yield inputChar == Character.toUpperCase(answer.charAt(0));
            }
            case TRUE_OR_FALSE, IDENTIFICATION -> answer.equalsIgnoreCase(playerAnswer.trim());
        };
    }

    public String getPrompt() {
        StringBuilder builder = new StringBuilder();

        switch (type) {
            case MULTIPLE_CHOICE -> {
                builder.append(MessagesConfiguration.oxMultipleChoiceQuestionFormat.replace("%question_text%", question)).append("\n");
                char letter = 'A';
                for (String choice : choices) {
                    builder.append(MessagesConfiguration.oxMultipleChoiceOptionFormat
                            .replace("%choice_letter%", String.valueOf(letter))
                            .replace("%choice_text%", choice)).append("\n");
                    letter++;
                }
            }
            case TRUE_OR_FALSE -> {
                builder.append(MessagesConfiguration.oxTrueFalseQuestionFormat.replace("%question_text%", question)).append("\n");
                builder.append(MessagesConfiguration.oxTrueFalseOptionFormat.replace("%choice_letter%", "A").replace("%choice_text%", "True")).append("\n");
                builder.append(MessagesConfiguration.oxTrueFalseOptionFormat.replace("%choice_letter%", "B").replace("%choice_text%", "False")).append("\n");
            }
            case IDENTIFICATION -> builder.append(MessagesConfiguration.oxIdentificationQuestionFormat.replace("%question_text%", question));
        }

        return builder.toString().trim();
    }

    public String answer() {
        if (answer == null) return null;
        return switch (type) {
            case MULTIPLE_CHOICE -> {
                char letter = Character.toUpperCase(answer.charAt(0));
                int index = letter - 'A';
                yield (choices != null && index >= 0 && index < choices.size()) ? choices.get(index) : null;
            }
            case TRUE_OR_FALSE, IDENTIFICATION -> answer.trim();
        };
    }

    public enum Type {
        MULTIPLE_CHOICE,
        TRUE_OR_FALSE,
        IDENTIFICATION
    }
}
