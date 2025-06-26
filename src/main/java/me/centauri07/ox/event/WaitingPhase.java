package me.centauri07.ox.event;

import me.centauri07.ox.config.MessagesConfiguration;
import me.centauri07.ox.utility.Countdown;
import org.bukkit.plugin.java.JavaPlugin;

public class WaitingPhase extends EventPhase {

    private final Countdown countdown;

    public WaitingPhase(JavaPlugin plugin, OxEvent oxEvent) {
        super(plugin, oxEvent);

        countdown = new Countdown(plugin, oxEvent.options.waitingTime(), count -> {

            if (count % 10 == 0 || count < 5) {
                oxEvent.sendEventMessage(MessagesConfiguration.eventStartCountdownMessage.replace("%seconds%", count + ""));
            }

        }, oxEvent::nextPhase);
    }

    @Override
    protected void start() {
        countdown.start();
    }
}
