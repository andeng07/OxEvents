package me.centauri07.ox.event.parkour;

import me.centauri07.ox.config.MessagesConfiguration;
import me.centauri07.ox.event.Options;
import me.centauri07.ox.event.OxEvent;
import me.centauri07.ox.event.WaitingPhase;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ParkourEvent extends OxEvent {

    private final JavaPlugin plugin;
    public final ParkourEventSettings settings;

    public ParkourEvent(JavaPlugin plugin, Options options, ParkourEventSettings settings) {
        super(plugin, options);

        this.plugin = plugin;
        this.settings = settings;

        setEventPhases(new WaitingPhase(plugin, this), new ParkourPhase(plugin, this, settings));
    }

    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);

        player.teleport(settings.returnLocation().asBukkitLocation());
    }

    @Override
    public void start() {
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(
                MessagesConfiguration.prefix
        ).append(MiniMessage.miniMessage().deserialize(MessagesConfiguration.parkourInitializeMessage)));
    }

    @Override
    public void end() {
        getEventPlayers().forEach(eventPlayer -> {
            Player player = eventPlayer.asPlayer();

            if (player != null) {
                player.teleport(settings.returnLocation().asBukkitLocation());
            }
        });
    }
}
