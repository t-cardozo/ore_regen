package com.troycardozo.myPlugin.config;

import java.io.File;
import java.io.IOException;

import com.troycardozo.myPlugin.App;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class YmlConfig {

    final App plugin;
    final String filename;
    private FileConfiguration config = null;
    private File configFile = null;

    public YmlConfig(App instance, String name) {
        plugin = instance;
        filename = name;
    }

    public <T> void set(String path, T val) {
        getConfig().set(path, val);
        saveConfig();
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }

        return config;
    }

    public void reloadConfig() {

        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), filename);
        }

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource(filename, false);
        }

        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

    public void saveConfig() {
        if ((config == null) || (configFile == null)) {
            return;
        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}