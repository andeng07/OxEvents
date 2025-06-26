package me.centauri07.ox.event.trivia;

import me.centauri07.ox.event.EventPlayer;

import java.util.*;
import java.util.stream.Collectors;

public class QuestionRound {

    private final Question question;
    private final Map<UUID, Boolean> participants;

    public QuestionRound(Question question, List<EventPlayer> participants) {
        this.question = question;

        this.participants = participants.stream()
                .collect(Collectors.toMap(EventPlayer::getUniqueId, p -> false));
    }

    public boolean hasAnswered(EventPlayer player) {
        Boolean value = participants.get(player.getUniqueId());

        return (player.getState() == EventPlayer.State.ELIMINATED) ||
                (player.getState() == EventPlayer.State.ALIVE && Boolean.TRUE.equals(value));
    }

    public boolean isParticipant(EventPlayer eventPlayer) {
        return participants.containsKey(eventPlayer.getUniqueId());
    }

    public boolean answer(EventPlayer player, String input) {
        UUID id = player.getUniqueId();

        boolean correct = question.isCorrect(input);
        participants.put(id, correct);

        if (!correct) {
            player.setState(EventPlayer.State.ELIMINATED);
        }

        return correct;
    }

    public String getPrompt() {
        return question.getPrompt();
    }

    public Question getQuestion() {
        return question;
    }

    public Map<UUID, Boolean> getParticipants() {
        return participants;
    }
}
