package me.centauri07.ox.config;

import me.centauri07.ox.event.trivia.QuestionRound;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class OxEventConfig extends Configuration {

    private static List<QuestionRound> questionRoundList;

    public OxEventConfig(JavaPlugin plugin) {
        super(plugin, "ox-config");
    }

    @Override
    protected void setup() {
        questionRoundList = QuestionRound.fromConfig(configuration);
    }
}
