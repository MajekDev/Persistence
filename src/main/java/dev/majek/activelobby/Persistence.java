package dev.majek.activelobby;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Persistence extends JavaPlugin {

    public static Persistence instance;
    public Persistence() {
        instance = this;
    }
    public static Persistence getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new PluginEventHandler(), this);
        reloadConfig();

        Bukkit.getScheduler().runTaskTimer(this, PluginEventHandler::reset,0, 20L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
