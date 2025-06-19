package me.centauri07.ox.utility;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Countdown extends BukkitRunnable {
    private final JavaPlugin plugin;
    private int seconds;
    private final Runnable onFinish;
    private final java.util.function.IntConsumer onTick;

    public Countdown(JavaPlugin plugin, int seconds, java.util.function.IntConsumer onTick, Runnable onFinish) {
        this.plugin = plugin;
        this.seconds = seconds;
        this.onTick = onTick;
        this.onFinish = onFinish;
    }

    public void start() {
        this.runTaskTimer(plugin, 0L, 20L); // 20 ticks = 1 second
    }

    @Override
    public void run() {
        if (seconds <= 0) {
            this.cancel();
            onFinish.run();
            return;
        }

        onTick.accept(seconds);
        seconds--;
    }

    public int getSeconds() {
        return seconds;
    }
}
