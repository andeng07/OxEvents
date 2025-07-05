package me.centauri07.ox.config;

import me.centauri07.ox.event.EventReward;
import me.centauri07.ox.event.trivia.Question;
import me.centauri07.ox.event.trivia.TriviaEventSettings;
import org.bukkit.plugin.java.JavaPlugin;

public class TriviaEventConfig extends Configuration {

    public TriviaEventSettings triviaEventSettings;

    public TriviaEventConfig(JavaPlugin plugin) {
        super(plugin, "ox-config");
    }

    @Override
    protected void setup() {
        triviaEventSettings = new TriviaEventSettings(
                Question.fromConfigList(configuration),
                EventReward.fromConfig("reward", configuration.getConfigurationSection("reward"))
        );
    }

    @Override
    public void save() {
        super.save();
    }
}
