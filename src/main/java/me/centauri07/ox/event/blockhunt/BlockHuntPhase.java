package me.centauri07.ox.event.blockhunt;

import me.centauri07.ox.config.BlockHuntDataStore;
import me.centauri07.ox.config.MessagesConfiguration;
import me.centauri07.ox.event.EventPhase;
import me.centauri07.ox.event.OxEvent;
import me.centauri07.ox.utility.Countdown;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class BlockHuntPhase extends EventPhase {
    private final BlockHuntEventSettings settings;
    private final Countdown countdown;

    // Stores stringified block locations and whether they’re claimed
    private final Map<String, Boolean> claimable = new HashMap<>();

    public BlockHuntPhase(JavaPlugin plugin, OxEvent oxEvent, BlockHuntEventSettings settings, BlockHuntDataStore dataStore) {
        super(plugin, oxEvent);
        this.settings = settings;

        setListeners(List.of(new BlockHuntPhaseListener(settings, dataStore)));

        // Initialize claimable blocks
        for (BlockData data : dataStore.getAll()) {
            System.out.println(data.location().asKey());

            claimable.put(data.location().asKey(), false);
        }

        countdown = new Countdown(plugin, settings.timeLimit(),
                (time) -> oxEvent.sendActionBar(MessagesConfiguration.eventTimeRemainingMessage.replace("%time_remaining%", String.valueOf(time))),
                oxEvent::nextPhase
        );
    }

    @Override
    protected void start() {
        oxEvent.sendEventMessage(MessagesConfiguration.hiddenBlocksStartMessage);

        oxEvent.getEventPlayers().forEach(eventPlayer -> {
            Player player = eventPlayer.asPlayer();
            if (player != null) {
                player.teleport(settings.huntLocation().asBukkitLocation());
            }
        });

        countdown.start();
    }

    @Override
    protected void end() {
        if (countdown != null && !countdown.isCancelled()) countdown.cancel();

        claimable.clear();

        oxEvent.sendEventMessage(MessagesConfiguration.hiddenBlocksEndMessage);
    }

    public boolean isClaimed(Location loc) {
        String key = me.centauri07.ox.utility.Location.getLocation(loc).asKey();

        System.out.println(key);

        return claimable.getOrDefault(key, false);
    }

    public boolean isClaimable(Location loc) {
        return claimable.containsKey(me.centauri07.ox.utility.Location.getLocation(loc).asKey());
    }

    public void markClaimed(Location loc) {
        String key = me.centauri07.ox.utility.Location.getLocation(loc).asKey();
        if (claimable.containsKey(key) && !claimable.get(key)) {
            claimable.put(key, true);

            // Auto-finish if all are claimed
            if (getClaimed() >= getClaimable()) {
                oxEvent.nextPhase(); // triggers end()
            }
        }
    }

    public int getClaimed() {
        return (int) claimable.values().stream().filter(claimed -> claimed).count();
    }

    public int getClaimable() {
        return claimable.size();
    }
}
