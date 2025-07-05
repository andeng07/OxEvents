package me.centauri07.ox.event.trivia;

import me.centauri07.ox.event.EventReward;

import java.util.List;

public record TriviaEventSettings(List<Question> questions, EventReward reward) { }
