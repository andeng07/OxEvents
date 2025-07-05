package me.centauri07.ox.command;

import me.centauri07.ox.config.BlockHuntConfig;
import org.bukkit.command.Command;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EventSetupTabCompleter implements TabCompleter {

    private final BlockHuntConfig blockHuntConfig;

    public EventSetupTabCompleter(BlockHuntConfig blockHuntConfig) {
        this.blockHuntConfig = blockHuntConfig;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("parkour", "block_hunt").stream()
                    .filter(opt -> opt.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            List<String> suggestions = new ArrayList<>();

            String eventType = args[0].toLowerCase();

            if (eventType.equals("parkour")) {
                suggestions = Arrays.asList("spawn", "return", "start", "end");
            } else if (eventType.equals("block_hunt")) {
                suggestions = Arrays.asList("spawn", "return", "hide");
            }

            return suggestions.stream()
                    .filter(opt -> opt.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("block_hunt") && args[1].equalsIgnoreCase("hide")) {
            return blockHuntConfig.settings.rewardList().stream()
                    .map(reward -> reward.id)
                    .filter(key -> key.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
