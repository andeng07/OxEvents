package me.centauri07.ox.command;

import me.centauri07.ox.event.OxEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventCommandTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {

        if (args.length == 1) {
            return Stream.of("start", "dolacz", "join", "leave", "opusc", "stop")
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("start")) {
            return Arrays.stream(OxEvent.Type.values())
                    .map(type -> type.id)
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("start")) {
            return Stream.of("<player_limit>")
                    .filter(s -> s.startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("start")) {
            return Stream.of("<waiting_time>")
                    .filter(s -> s.startsWith(args[3].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return List.of();
    }
}
