package me.centauri07.ox.event.trivia;

import me.centauri07.ox.config.MessagesConfiguration;
import me.centauri07.ox.event.Options;
import me.centauri07.ox.event.OxEvent;
import me.centauri07.ox.event.WaitingPhase;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TriviaEvent extends OxEvent {

    public TriviaEvent(JavaPlugin plugin, Options options, TriviaEventSettings settings) {
        super(plugin, options);

        setEventPhases(
                new WaitingPhase(plugin, this),
                new QuestionPhase(plugin, this, settings)
        );
    }


    @Override
    public void start() {
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(
                MessagesConfiguration.prefix
        ).append(MiniMessage.miniMessage().deserialize(MessagesConfiguration.oxInitializeMessage)));
    }
}
