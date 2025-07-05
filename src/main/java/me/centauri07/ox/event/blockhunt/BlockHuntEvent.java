package me.centauri07.ox.event.blockhunt;

import me.centauri07.ox.config.BlockHuntDataStore;
import me.centauri07.ox.config.MessagesConfiguration;
import me.centauri07.ox.event.Options;
import me.centauri07.ox.event.OxEvent;
import me.centauri07.ox.event.WaitingPhase;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockHuntEvent extends OxEvent {
    private final JavaPlugin plugin;
    public final BlockHuntEventSettings settings;
    public final BlockHuntDataStore dataStore;

    public BlockHuntEvent(JavaPlugin plugin, Options options, BlockHuntEventSettings settings, BlockHuntDataStore dataStore) {
        super(plugin, options);
        this.plugin = plugin;
        this.settings = settings;
        this.dataStore = dataStore;

        setEventPhases(new WaitingPhase(plugin, this), new BlockHuntPhase(plugin, this, settings, dataStore));
    }

    @Override
    public void start() {
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(
                MessagesConfiguration.prefix
        ).append(MiniMessage.miniMessage().deserialize(MessagesConfiguration.hiddenBlocksInitializeMessage)));
    }

    @Override
    public void end() {
        getEventPlayers().forEach(eventPlayer -> {
            Player player = eventPlayer.asPlayer();

            if (player != null) {
                player.teleport(settings.returnLocation().asBukkitLocation());
            }
        });

        dataStore.getAll().forEach(block -> block.location().asBukkitLocation().getBlock().setType(block.material()));

        super.end();
    }

    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);

        player.teleport(settings.returnLocation().asBukkitLocation());
    }
}