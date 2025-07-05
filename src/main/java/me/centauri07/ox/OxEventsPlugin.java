package me.centauri07.ox;

import me.centauri07.ox.command.EventCommand;
import me.centauri07.ox.command.EventCommandTabCompleter;
import me.centauri07.ox.command.EventSetupCommand;
import me.centauri07.ox.command.EventSetupTabCompleter;
import me.centauri07.ox.config.*;
import me.centauri07.ox.listener.BlockBreakListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class OxEventsPlugin extends JavaPlugin {

    private MessagesConfiguration messagesConfiguration;
    private TriviaEventConfig triviaEventConfig;
    private ParkourEventConfig parkourEventConfig;
    private BlockHuntConfig blockHuntConfig;

    private BlockHuntDataStore blockHuntDataStore;

    @Override
    public void onEnable() {
        messagesConfiguration = new MessagesConfiguration(this);
        triviaEventConfig = new TriviaEventConfig(this);
        parkourEventConfig = new ParkourEventConfig(this);
        blockHuntConfig = new BlockHuntConfig(this);

        messagesConfiguration.load();
        triviaEventConfig.load();
        parkourEventConfig.load();
        blockHuntConfig.load();

        blockHuntDataStore = new BlockHuntDataStore(this);

        blockHuntDataStore.load();

        Bukkit.getPluginCommand("event").setExecutor(
                new EventCommand(
                        this,
                        triviaEventConfig,
                        parkourEventConfig,
                        blockHuntConfig,
                        blockHuntDataStore
                )
        );

        Bukkit.getPluginCommand("event").setTabCompleter(
                new EventCommandTabCompleter()
        );

        Bukkit.getPluginCommand("eventsetup").setExecutor(
                new EventSetupCommand(
                        parkourEventConfig,
                        blockHuntConfig,
                        blockHuntDataStore
                )
        );

        Bukkit.getPluginCommand("eventsetup").setTabCompleter(
                new EventSetupTabCompleter(blockHuntConfig)
        );

        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(blockHuntDataStore), this);
    }

}
