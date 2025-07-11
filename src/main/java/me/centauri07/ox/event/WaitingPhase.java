package me.centauri07.ox.event;

import me.centauri07.ox.config.MessagesConfiguration;
import me.centauri07.ox.utility.Countdown;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WaitingPhase extends EventPhase {

    private final Countdown countdown;

    public WaitingPhase(JavaPlugin plugin, OxEvent oxEvent) {
        super(plugin, oxEvent);

        countdown = new Countdown(plugin, oxEvent.options.waitingTime(), count -> {

            for (Player player : Bukkit.getOnlinePlayers()) {
                String message = MessagesConfiguration.eventStartCountdownMessage
                        .replace("%time_remaining%", String.valueOf(count))
                        .replace("%current_players%", String.valueOf(oxEvent.getEventPlayers(eventPlayer -> !eventPlayer.asPlayer().hasPermission("oxevent.admin")).size()))
                        .replace("%required_players%", String.valueOf(oxEvent.options.playerLimit()));

                player.sendActionBar(MiniMessage.miniMessage().deserialize(message));
            }

        }, oxEvent::nextPhase);
    }

    @Override
    protected void start() {
        countdown.start();
    }


    @Override
    protected void end() {
        if (countdown != null && !countdown.isCancelled()) countdown.cancel();
    }
}
