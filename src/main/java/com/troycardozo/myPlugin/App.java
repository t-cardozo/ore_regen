package com.troycardozo.myPlugin;

import java.util.HashMap;
import com.troycardozo.myPlugin.commands.OreCommand;
import com.troycardozo.myPlugin.commands.OreTabCompleter;
import com.troycardozo.myPlugin.config.YmlConfig;
import com.troycardozo.myPlugin.listeners.BlockBreak;
import com.troycardozo.myPlugin.listeners.BlockPlaced;
import com.troycardozo.myPlugin.listeners.PlayerInteract;

import org.bukkit.plugin.java.JavaPlugin;

public class App extends JavaPlugin {
    public String configFileName = "-oreGenConfig.yml";
    public YmlConfig oreGenConfig;
    public HashMap<String, YmlConfig> oreGenConfigs = new HashMap<String, YmlConfig>();
    public Ore ore;

    @Override
    public void onEnable() {
        setup();
    }

    @Override
    public void onDisable() {

    }

    void setup() {
        this.saveDefaultConfig();
        this.ore = new Ore(this);

        ore.loadConfigs();
        ore.loadConfigVariables(this.getConfig().getString("selected-entity"), true);

        getCommand("ore").setExecutor(new OreCommand(this));
        getCommand("ore").setTabCompleter(new OreTabCompleter(this));

        getServer().getPluginManager().registerEvents(new BlockPlaced(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreak(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteract(this), this);
    }

}
