package me.centauri07.ox.command;

import me.centauri07.ox.config.*;
import me.centauri07.ox.event.EventPlayer;
import me.centauri07.ox.event.Options;
import me.centauri07.ox.event.OxEvent;
import me.centauri07.ox.event.blockhunt.BlockHuntEvent;
import me.centauri07.ox.event.parkour.ParkourEvent;
import me.centauri07.ox.event.trivia.TriviaEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    private final TriviaEventConfig triviaEventSettings;
    private final ParkourEventConfig parkourEventSettings;
    private final BlockHuntConfig blockHuntEventSettings;
    private final BlockHuntDataStore blockHuntDataStore;

    public EventCommand(
            JavaPlugin plugin,
            TriviaEventConfig triviaEventConfig,
            ParkourEventConfig parkourEventConfig,
            BlockHuntConfig blockHuntConfig,
            BlockHuntDataStore blockHuntDataStore
    ) {
        this.plugin = plugin;
        this.triviaEventSettings = triviaEventConfig;
        this.parkourEventSettings = parkourEventConfig;
        this.blockHuntEventSettings = blockHuntConfig;
        this.blockHuntDataStore = blockHuntDataStore;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can execute this command!"));

            return true;
        }

        String eventTypesEnumerated = Arrays.stream(OxEvent.Type.values()).map(event -> event.id).collect(Collectors.joining(" | "));

        String usage = "<red>Correct usage: /event <start | join | dolacz | leave | opusc | stop> <" + eventTypesEnumerated + "> <player_limit> <waiting_time>";

        if (args.length == 0) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(usage));
            return true;
        }

        switch (args[0]) {
            case "start" -> {
                if (!player.hasPermission("event.start")) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>You don't have permission to execute this command!"));

                    return true;
                }

                if (args.length != 4) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(usage));

                    return true;
                }

                if (OxEvent.currentEvent != null) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>An event is currently in progress!"));
                    return true;
                }

                Optional<OxEvent.Type> type = Arrays.stream(OxEvent.Type.values()).filter(eventType -> Objects.equals(eventType.id, args[1])).findFirst();

                if (type.isEmpty()) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(usage));

                    return true;
                }

                int playerLimit;

                try {
                    playerLimit = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Player limit must be a number"));

                    return true;
                }

                if (playerLimit < 1) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Player limit cannot be less than 1"));

                    return true;
                }

                int waitingTime;

                try {
                    waitingTime = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Waiting time must be a number"));

                    return true;
                }

                if (waitingTime < 1) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Waiting time cannot be less than 1"));

                    return true;
                }

                Options options = new Options(type.get(), playerLimit, waitingTime);

                switch (type.get()) {
                    case OxEvent.Type.TRIVIA ->
                            OxEvent.currentEvent = new TriviaEvent(plugin, options, triviaEventSettings.triviaEventSettings);

                    case OxEvent.Type.PARKOUR ->
                            OxEvent.currentEvent = new ParkourEvent(plugin, options, parkourEventSettings.parkourEventSettings);

                    case OxEvent.Type.BLOCK_HUNT ->
                            OxEvent.currentEvent = new BlockHuntEvent(plugin, options, blockHuntEventSettings.settings, blockHuntDataStore);

                    default -> sender.sendMessage(MiniMessage.miniMessage().deserialize(usage));
                }

                OxEvent currentEvent = OxEvent.currentEvent;

                if (currentEvent != null) {
                    currentEvent.nextPhase();

                    currentEvent.addPlayer(player, EventPlayer.State.ELIMINATED);
                }
            }

            case "join", "dolacz" -> {
                if (args.length != 1) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(usage));

                    return true;
                }

                OxEvent currentEvent = OxEvent.currentEvent;

                if (currentEvent == null) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>There are no current active event"));

                    return true;
                }

                currentEvent.addPlayer(player, EventPlayer.State.ALIVE);
            }

            case "leave", "opusc" -> {
                if (args.length != 1) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(usage));

                    return true;
                }

                OxEvent currentEvent = OxEvent.currentEvent;

                if (currentEvent == null) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>There are no current active event"));

                    return true;
                }

                if (!currentEvent.isPlayer(player)) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>You are not a member of an event"));
                    return true;
                }

                currentEvent.removePlayer(player);

                player.sendMessage(MiniMessage.miniMessage().deserialize(MessagesConfiguration.eventLeaveMessage));
            }

            case "stop" -> {
                if (args.length != 1) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(usage));

                    return true;
                }

                if (!player.hasPermission("event.stop")) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>You don't have permission to execute this command!"));

                    return true;
                }

                OxEvent currentEvent = OxEvent.currentEvent;

                if (currentEvent == null) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>There are no current active event"));

                    return true;
                }

                currentEvent.forceStop();

            }

            default ->
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Correct usage: /event <start | join> [" + eventTypesEnumerated + "]"));
        }


        return true;
    }


}
