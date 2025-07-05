package me.centauri07.ox.event;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventReward {
    public final String id;
    public final String name;
    public final List<String> commands = new ArrayList<>();

    private EventReward(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static EventReward of(String id, String name) {
        return new EventReward(id, name);
    }

    public EventReward withCommand(List<String> commands) {
        this.commands.addAll(commands);
        return this;
    }

    public EventReward withCommand(String... commands) {
        withCommand(List.of(commands));
        return this;
    }

    public void apply(Player player) {
        commands.forEach(command ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()))
        );
    }

    // Loads a single reward by ID
    public static EventReward fromConfig(String id, ConfigurationSection section) {
        String name = section.getString("name");
        List<String> commands = section.getStringList("commands");

        return EventReward.of(id, name).withCommand(commands.toArray(String[]::new));
    }

    public static List<EventReward> fromConfig(ConfigurationSection rewardsSection) {
        if (rewardsSection == null) return Collections.emptyList();

        List<EventReward> rewards = new ArrayList<>();

        for (String id : rewardsSection.getKeys(false)) {
            ConfigurationSection section = rewardsSection.getConfigurationSection(id);
            if (section == null) continue;

            rewards.add(fromConfig(id, section));
        }

        return rewards;
    }
}