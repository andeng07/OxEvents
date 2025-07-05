package me.centauri07.ox.event.blockhunt;

import me.centauri07.ox.config.BlockHuntDataStore;
import me.centauri07.ox.config.MessagesConfiguration;
import me.centauri07.ox.event.EventPhaseListener;
import me.centauri07.ox.event.EventPlayer;
import me.centauri07.ox.event.OxEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Objects;

public class BlockHuntPhaseListener extends EventPhaseListener {

    private final BlockHuntEventSettings settings;
    private final BlockHuntDataStore dataStore;

    public BlockHuntPhaseListener(BlockHuntEventSettings settings, BlockHuntDataStore dataStore) {
        this.settings = settings;
        this.dataStore = dataStore;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!(OxEvent.currentEvent instanceof BlockHuntEvent blockHuntEvent)) return;
        if (!(blockHuntEvent.getCurrentPhase() instanceof BlockHuntPhase phase)) return;

        EventPlayer eventPlayer = blockHuntEvent.getEventPlayer(event.getPlayer());
        if (eventPlayer == null) return;

        Block block = event.getBlock();
        Location location = block.getLocation();

        // Not a reward block
        if (!phase.isClaimable(location)) return;

        // Already claimed
        if (phase.isClaimed(location)) return;

        if (eventPlayer.getState() != EventPlayer.State.ALIVE) {
            event.setCancelled(true);
            return;
        }

        dataStore.get(location).ifPresent((me.centauri07.ox.event.blockhunt.BlockData data) -> {
            blockHuntEvent.sendEventMessage(MessagesConfiguration.hiddenBlocksFoundReward
                    .replace("%reward_name%", data.reward())
                    .replace("%player_name%", event.getPlayer().getName())
                    .replace("%claimed_count%", String.valueOf(phase.getClaimed() + 1))
                    .replace("%total_rewards%", String.valueOf(phase.getClaimable()))
            );

            settings.rewardList().stream()
                    .filter(reward -> Objects.equals(reward.id, data.reward()))
                    .findFirst().ifPresent(reward -> reward.apply(event.getPlayer()));

            phase.markClaimed(location);
        });
    }
}
