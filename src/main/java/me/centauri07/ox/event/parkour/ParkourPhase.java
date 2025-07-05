package me.centauri07.ox.event.parkour;

import me.centauri07.ox.config.MessagesConfiguration;
import me.centauri07.ox.event.EventPhase;
import me.centauri07.ox.event.EventPlayer;
import me.centauri07.ox.event.OxEvent;
import me.centauri07.ox.utility.Countdown;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ParkourPhase extends EventPhase {
    private final ParkourEventSettings settings;
    private final Countdown countdown;

    private final List<UUID> started = new ArrayList<>();
    private final List<UUID> finished = new ArrayList<>();

    public ParkourPhase(JavaPlugin plugin, OxEvent oxEvent, ParkourEventSettings settings) {
        super(plugin, oxEvent);

        this.settings = settings;

        setListeners(List.of(new ParkourPhaseListener(settings)));

        countdown = new Countdown(plugin, settings.timeLimit(),
                (time) -> oxEvent.sendActionBar(MessagesConfiguration.eventTimeRemainingMessage.replace("%time_remaining%", time + "")),
                oxEvent::nextPhase /* end game */);
    }

    @Override
    protected void start() {
        oxEvent.sendEventMessage(MessagesConfiguration.parkourStartMessage);

        oxEvent.getEventPlayers().forEach(eventPlayer -> {
            Player player = eventPlayer.asPlayer();

            if (player != null) {
                player.setAllowFlight(false);
                player.setFlying(false);

                player.clearActivePotionEffects();

                player.teleport(settings.parkourLocation().asBukkitLocation());
            }
        });

        countdown.start();
    }

    @Override
    protected void end() {
        if (countdown != null && !countdown.isCancelled()) countdown.cancel();

        List<EventPlayer> winners = finished.stream().map(oxEvent::getEventPlayer).toList();

        winners.forEach(eventPlayer -> {
            Player player = eventPlayer.asPlayer();

            if (player == null) return;

            settings.reward().apply(player);
        });

        started.clear();
        finished.clear();

        oxEvent.sendEventMessage(MessagesConfiguration.parkourEndMessage);

        oxEvent.sendEventMessage(
                MessagesConfiguration.playerWinMessage.replace("%winners%", winners.isEmpty() ? "No one" :
                        winners.stream()
                                .map(winner -> winner.asPlayer().getName())
                                .collect(Collectors.joining(", "))));
    }

    public boolean isStarted(EventPlayer eventPlayer) {
        return started.contains(eventPlayer.getUniqueId());
    }

    public void startParkour(EventPlayer eventPlayer) {
        if (isStarted(eventPlayer)) return;

        started.add(eventPlayer.getUniqueId());
    }

    public boolean isParkourFinished(EventPlayer eventPlayer) {
        return finished.contains(eventPlayer.getUniqueId());
    }

    public void finishParkour(EventPlayer eventPlayer) {
        if (!isStarted(eventPlayer)) return;

        finished.add(eventPlayer.getUniqueId());

        if (finished.size() >= settings.winnerCount()) {
            oxEvent.nextPhase();
        }
    }

    public void eliminateParkour(EventPlayer eventPlayer) {
        if (!isStarted(eventPlayer)) return;

        eventPlayer.setState(EventPlayer.State.ELIMINATED);
        started.remove(eventPlayer.getUniqueId());
    }

    public List<UUID> getFinishers() {
        return finished;
    }
}
