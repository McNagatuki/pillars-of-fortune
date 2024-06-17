package com.github.mcnagatuki.pillarsoffortune;

import org.bukkit.plugin.java.JavaPlugin;

public final class PillarsOfFortune extends JavaPlugin {
    public static PillarsOfFortune plugin;
    public ChestManager chestManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        chestManager = new ChestManager();

        this.getCommand("pof").setExecutor(new CommandManager());
        getLogger().info("Pillars of Fortune has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Pillars of Fortune has been disabled!");
    }
}
