package me.centauri07.ox.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MessagesConfiguration extends Configuration {

    public static String prefix;
    public static String countdownMessage;

    public MessagesConfiguration(JavaPlugin plugin) {
        super(plugin, "messages");
    }

    @Override
    protected void setup(YamlConfiguration configuration) {

    }

}
