package me.centauri07.ox.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EventReward {

    public List<ItemStack> items = new ArrayList<>();
    public List<String> commands = new ArrayList<>();

    public EventReward ofItem(Material material, int amount) {
        items.add(new ItemStack(material, amount));
        return this;
    }

    public EventReward ofItem(ItemStack stack) {
        items.add(stack);
        return this;
    }

    public EventReward ofCommand(String command) {
        commands.add(command);

        return this;
    }

    public void apply(Player player) {
        commands.forEach(command -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        });

        player.getInventory().addItem(items.toArray(new ItemStack[0]));
    }
}
