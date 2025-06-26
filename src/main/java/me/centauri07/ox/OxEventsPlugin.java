package me.centauri07.ox;

import me.centauri07.ox.command.EventCommand;
import me.centauri07.ox.config.MessagesConfiguration;
import me.centauri07.ox.config.TriviaEventConfig;
import me.centauri07.ox.config.ParkourEventConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class OxEventsPlugin extends JavaPlugin {

    private MessagesConfiguration messagesConfiguration;
    private TriviaEventConfig triviaEventConfig;
    private ParkourEventConfig parkourEventConfig;

    @Override
    public void onEnable() {
        messagesConfiguration = new MessagesConfiguration(this);
        triviaEventConfig = new TriviaEventConfig(this);
        parkourEventConfig = new ParkourEventConfig(this);

        messagesConfiguration.load();
        triviaEventConfig.load();
        parkourEventConfig.load();

        Bukkit.getPluginCommand("event").setExecutor(
                new EventCommand(
                        this,
                        triviaEventConfig.triviaEventSettings,
                        parkourEventConfig.parkourEventSettings
                )
        );
    }

}
