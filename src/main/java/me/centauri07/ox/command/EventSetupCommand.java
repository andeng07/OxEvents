package me.centauri07.ox.command;

import me.centauri07.ox.config.BlockHuntConfig;
import me.centauri07.ox.config.BlockHuntDataStore;
import me.centauri07.ox.config.ParkourEventConfig;
import me.centauri07.ox.event.OxEvent;
import me.centauri07.ox.event.blockhunt.BlockData;
import me.centauri07.ox.event.blockhunt.BlockHuntEventSettings;
import me.centauri07.ox.event.parkour.ParkourEventSettings;
import me.centauri07.ox.utility.Location;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EventSetupCommand implements CommandExecutor {

    private final ParkourEventConfig parkourEventConfig;
    private final BlockHuntConfig blockHuntConfig;
    private final BlockHuntDataStore dataStore;

    public EventSetupCommand(ParkourEventConfig parkourEventConfig, BlockHuntConfig blockHuntConfig, BlockHuntDataStore dataStore) {
        this.parkourEventConfig = parkourEventConfig;
        this.blockHuntConfig = blockHuntConfig;
        this.dataStore = dataStore;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can execute this command!</red>"));
            return true;
        }

        if (!player.hasPermission("event.setup")) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>You don't have the permission to execute this command."));
            return true;
        }

        if (OxEvent.currentEvent != null) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Cannot modify config while there's an ongoing event!</red>"));
            return true;
        }

        String usage = """
                <red><bold>Correct Usage</bold></red>
                <gray>❘</gray> <gold>Set spawn and return:</gold>
                <gray>   ➥</gray> <green>/eventsetup <parkour|block_hunt> <spawn|return></green>
                
                <gray>❘</gray> <gold>Set parkour start and end points:</gold>
                <gray>   ➥</gray> <blue>/eventsetup parkour <start|end></blue>
                
                <gray>❘</gray> <gold>Set hidden block with reward (Block Hunt):</gold>
                <gray>   ➥</gray> <aqua>/eventsetup block_hunt hide <reward></aqua>
                """.stripIndent();

        if (args.length < 2) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(usage));
            return true;
        }

        String eventType = args[0].toLowerCase();
        String action = args[1].toLowerCase();

        switch (eventType) {
            case "parkour" -> {
                switch (action) {
                    case "spawn", "return" -> {
                        parkourEventConfig.parkourEventSettings = new ParkourEventSettings(
                                action.equals("spawn") ? Location.getLocation(player.getLocation()) : parkourEventConfig.parkourEventSettings.parkourLocation(),
                                action.equals("return") ? Location.getLocation(player.getLocation()) : parkourEventConfig.parkourEventSettings.returnLocation(),
                                parkourEventConfig.parkourEventSettings.parkourStart(),
                                parkourEventConfig.parkourEventSettings.parkourEnd(),
                                parkourEventConfig.parkourEventSettings.winnerCount(),
                                parkourEventConfig.parkourEventSettings.timeLimit(),
                                parkourEventConfig.parkourEventSettings.reward(),
                                parkourEventConfig.parkourEventSettings.blockedCommands()
                        );

                        parkourEventConfig.save();

                        player.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<green>✔ Successfully set the <bold>" + action + "</bold> point for Parkour!</green>"
                        ));

                        return true;
                    }
                    case "start", "end" -> {
                        Block targetBlock = player.getTargetBlockExact(5);

                        if (targetBlock == null || targetBlock.getType() == Material.AIR || !targetBlock.getType().name().endsWith("_PRESSURE_PLATE")) {
                            player.sendMessage(MiniMessage.miniMessage().deserialize(
                                    "<red>Please look at a valid pressure plate block within 5 blocks to set as the " + action + " point.</red>"
                            ));
                            return true;
                        }

                        org.bukkit.Location location = targetBlock.getLocation();

                        parkourEventConfig.parkourEventSettings = new ParkourEventSettings(
                                parkourEventConfig.parkourEventSettings.parkourLocation(),
                                parkourEventConfig.parkourEventSettings.returnLocation(),
                                action.equals("start") ? Location.getLocation(location) : parkourEventConfig.parkourEventSettings.parkourStart(),
                                action.equals("end") ? Location.getLocation(location) : parkourEventConfig.parkourEventSettings.parkourEnd(),
                                parkourEventConfig.parkourEventSettings.winnerCount(),
                                parkourEventConfig.parkourEventSettings.timeLimit(),
                                parkourEventConfig.parkourEventSettings.reward(),
                                parkourEventConfig.parkourEventSettings.blockedCommands()
                        );

                        parkourEventConfig.save();

                        player.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<green>✔ " + action.substring(0, 1).toUpperCase() + action.substring(1) + " pressure plate set successfully for Parkour.</green>"
                        ));

                        return true;
                    }
                    default -> sender.sendMessage(MiniMessage.miniMessage().deserialize(usage));
                }
            }

            case "block_hunt" -> {
                switch (action) {
                    case "spawn", "return" -> {
                        blockHuntConfig.settings = new BlockHuntEventSettings(
                                action.equals("spawn") ? Location.getLocation(player.getLocation()) : blockHuntConfig.settings.huntLocation(),
                                action.equals("return") ? Location.getLocation(player.getLocation()) : blockHuntConfig.settings.returnLocation(),
                                blockHuntConfig.settings.timeLimit(),
                                blockHuntConfig.settings.rewardList()
                        );

                        blockHuntConfig.save();

                        player.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<green>✔ " + action.substring(0, 1).toUpperCase() + action.substring(1) + " point set for Block Hunt!</green>"
                        ));

                        return true;
                    }
                    case "hide" -> {
                        if (args.length < 3) {
                            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                                    "<red>Usage:</red> <aqua>/eventsetup block_hunt hide <reward></aqua>"
                            ));
                            return true;
                        }

                        Block targetBlock = player.getTargetBlockExact(5);

                        if (targetBlock == null || targetBlock.getType() == Material.AIR) {
                            player.sendMessage(MiniMessage.miniMessage().deserialize(
                                    "<red>Please look at a solid block within 5 blocks to hide a reward in.</red>"
                            ));
                            return true;
                        }

                        String rewardKey = args[2];

                        if (blockHuntConfig.settings.rewardList().stream().noneMatch(eventReward -> eventReward.id.equals(rewardKey))) {
                            player.sendMessage(MiniMessage.miniMessage().deserialize(
                                    "<red>Reward key <bold>" + rewardKey + "</bold> not found in config!</red>"
                            ));
                            return true;
                        }

                        dataStore.add(new BlockData(targetBlock.getType(), Location.getLocation(targetBlock.getLocation()), rewardKey));
                        dataStore.save();

                        player.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<green>✔ Hidden block with reward <bold>" + rewardKey + "</bold> successfully registered!</green>"
                        ));

                        return true;
                    }
                    default -> sender.sendMessage(MiniMessage.miniMessage().deserialize(usage));
                }
            }

            default -> sender.sendMessage(MiniMessage.miniMessage().deserialize(usage));
        }

        return true;
    }


}
