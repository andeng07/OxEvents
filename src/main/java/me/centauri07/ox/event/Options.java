package me.centauri07.ox.event;

public record Options(
    OxEvent.Type type,
    int playerLimit,
    int waitingTime
) {}
