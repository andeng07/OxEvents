package me.centauri07.ox.listener;

import me.centauri07.ox.config.BlockHuntDataStore;
import me.centauri07.ox.event.OxEvent;
import me.centauri07.ox.event.blockhunt.BlockHuntEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private final BlockHuntDataStore dataStore;

    public BlockBreakListener(BlockHuntDataStore dataStore) {
        this.dataStore = dataStore;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (dataStore.get(event.getBlock().getLocation()).isEmpty()) return;

        if (!event.getPlayer().hasPermission("event.hidden_blocks.block_break")) {
            event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<red>You cannot break this block!"));
            event.setCancelled(true);

            return;
        }

        if (OxEvent.currentEvent instanceof BlockHuntEvent blockHuntEvent) {
            if (blockHuntEvent.isPlayer(event.getPlayer())) return;

            event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<red>You cannot remove this hidden block while you're in game!"));
            return;
        }

        dataStore.remove(event.getBlock().getLocation());
        dataStore.save();
        event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<green>Hidden block has been removed."));
    }

}
