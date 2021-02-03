package dev.majek.persistence;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

public final class Persistence extends JavaPlugin {

    public static Persistence instance;
    public Persistence() {
        instance = this;
    }
    public static Persistence getCore() {
        return instance;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onEnable() {
        // Register listener class (basically the entire plugin)
        getServer().getPluginManager().registerEvents(new PluginEventHandler(), this);

        // Update config file
        saveDefaultConfig();
        File config = new File(getDataFolder(), "config.yml");
        try {
            ConfigUpdater.update(this, "config.yml", config, Collections.emptyList());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        reloadConfig();

        // Register metrics to track usage
        new Metrics(this, 10220);

        // This command force resets all blocks/entities that have been interacted with
        PluginCommand shopsCommand = getCommand("resetall");
        shopsCommand.setExecutor((sender, command, alias, args) -> {
            if (sender.isOp()) {
                PluginEventHandler.reset(true);
                sender.sendMessage(ChatColor.GREEN + "Interactables reset.");
            } else
                sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        });

        // Check every second to see if interactables need to be reset
        Bukkit.getScheduler().runTaskTimer(this, (Runnable) PluginEventHandler::reset,0, 20L);

        // Check to see if the world is empty and interactables need to be reset
        if (Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList())
                .contains(getConfig().getString("lobby-world-name")))
            Bukkit.getScheduler().runTaskTimer(this, () -> {
                if (Bukkit.getWorld(getConfig().getString("lobby-world-name")).getPlayers().size() == 0
                        && getConfig().getBoolean("reset-interactables-on-leave"))
                    PluginEventHandler.reset(true);
            },60L, 20L);
    }
}
