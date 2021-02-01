package dev.majek.activelobby;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class ActiveLobby extends JavaPlugin {

    public static ActiveLobby instance;
    public ActiveLobby() {
        instance = this;
    }
    public static ActiveLobby getInstance() {
        return instance;
    }

    public Map<Location, Material> blockMap = new HashMap<>();
    public Map<String, Boolean> doorMap = new HashMap<>();

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onEnable() {
        // Plugin startup logic
        getCommand("setlobbyworld").setExecutor(new SimpleCommand());
        getCommand("checkmap").setExecutor(new SimpleCommand());
        getCommand("revert").setExecutor(new SimpleCommand());
        getServer().getPluginManager().registerEvents(new PluginEventHandler(), this);
        reloadConfig();

        Bukkit.getScheduler().runTaskTimer(this, PluginEventHandler::reset,0, 20L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
