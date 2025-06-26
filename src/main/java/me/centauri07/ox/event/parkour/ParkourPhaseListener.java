package me.centauri07.ox.event.parkour;

import me.centauri07.ox.config.MessagesConfiguration;
import me.centauri07.ox.event.EventPhaseListener;
import me.centauri07.ox.event.EventPlayer;
import me.centauri07.ox.event.OxEvent;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ParkourPhaseListener extends EventPhaseListener {

    private final ParkourEventSettings settings;

    public ParkourPhaseListener(ParkourEventSettings settings) {
        this.settings = settings;
    }

    @EventHandler
    public void onPressurePlateTrigger(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) return;

        Block block = event.getClickedBlock();

        if (block == null) return;

        if (!event.getClickedBlock().getType().name().endsWith("_PRESSURE_PLATE")) return;

        if (OxEvent.currentEvent == null) return;

        if (!(OxEvent.currentEvent instanceof ParkourEvent parkourEvent)) return;

        if (parkourEvent.getCurrentPhase() == null) return;

        if (!(parkourEvent.getCurrentPhase() instanceof ParkourPhase parkourPhase)) return;

        EventPlayer eventPlayer = parkourEvent.getEventPlayer(event.getPlayer());

        if (eventPlayer == null) return;

        if (block.getX() == settings.parkourStart().x() &&
                block.getY() == settings.parkourStart().y() &&
                block.getZ() == settings.parkourStart().z()) {

            if (parkourPhase.isParkourFinished(eventPlayer)) {
                String message = MessagesConfiguration.parkourCompletedMessage
                        .replace("%current%", parkourPhase.getFinishers().size() + "")
                        .replace("%required%", parkourEvent.settings.winnerCount() + "");

                parkourEvent.sendEventMessage(message);

                return;
            }

            if (parkourPhase.isStarted(eventPlayer)) {
                parkourEvent.sendEventMessage(MessagesConfiguration.parkourAlreadyStarted);

                return;
            }

            parkourEvent.sendEventMessage(MessagesConfiguration.parkourStartMessage);
            parkourPhase.startParkour(eventPlayer);
        }

        if (block.getX() == settings.parkourEnd().x() &&
                block.getY() == settings.parkourEnd().y() &&
                block.getZ() == settings.parkourEnd().z()) {

            if (!parkourPhase.isStarted(eventPlayer)) {
                parkourEvent.sendEventMessage(MessagesConfiguration.parkourInvalidFinishAttempt);

                return;
            }

            parkourPhase.finishParkour(eventPlayer);

        }
    }

}
