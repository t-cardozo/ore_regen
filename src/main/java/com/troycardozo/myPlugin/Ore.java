package com.troycardozo.myPlugin;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.troycardozo.myPlugin.App;
import com.troycardozo.myPlugin.Utils.OtherF;
import com.troycardozo.myPlugin.Utils.RandomCollection;
import com.troycardozo.myPlugin.config.YmlConfig;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class Ore {

    final App plugin;
    final public String prefix = "[Ore] ";

    // Config variables:
    public String selectedEntity;
    public String defaultBlock;
    public String regenDuration;
    public Integer rangeDistance;
    public String configName;

    public HashMap<String, List<Integer>> allEntitiesMinMax = new HashMap<String, List<Integer>>(); // x & z min max for
                                                                                                    // all entities
    public HashMap<String, String> spawnedList = new HashMap<String, String>(); // spawned ores map
    public HashMap<String, Integer> rangeCoords = new HashMap<String, Integer>(); // range coords
    private RandomCollection<String> oreCollection = new RandomCollection<String>(); // percentage ore picker

    public Ore(App instance) {
        plugin = instance;
        getEntityList();
        setAllMinMaxEntities();
    }

    public void displayEntityList(Player player) {
        player.sendMessage(plugin.ore.prefix + "======================================");
        for (String entity : getEntityList()) {

            if (entity.equals(plugin.ore.selectedEntity)) {
                player.sendMessage(plugin.ore.prefix + ChatColor.GOLD + "Entity: " + entity + " <Selected>");
            } else {
                player.sendMessage(plugin.ore.prefix + "Entity: " + entity);
            }

        }
        player.sendMessage(
                plugin.ore.prefix + "============= Entity List: [" + getEntityList().size() + "] =============");
    }

    public void loadConfigs() {
        // loads all configs in a hashmap, which can be used anytime.
        plugin.oreGenConfigs.clear();
        File[] files = plugin.getDataFolder().listFiles();
        for (File file : files) {
            if (file.isFile()) {
                if (file.getName().contains("oreGenConfig")) {
                    String entityName = file.getName().split("-oreGenConfig")[0];
                    plugin.oreGenConfigs.put(entityName, new YmlConfig(plugin, file.getName()));
                    plugin.oreGenConfigs.get(entityName).set("general.name", entityName);

                } else {
                    // this will only run on first load of if someone deletes all the oreGenConfigs.
                    plugin.oreGenConfigs.put("default", new YmlConfig(plugin, "default" + plugin.configFileName));
                    plugin.oreGenConfigs.get("default").set("general.name", "default");
                }
            }
        }
    }

    public void addEntitytoList(String entity) {
        saveConfig("entity-list." + entity + ".coords", new HashMap<String, String>());
    }

    public void entityConfigMatch(Player player) {
        if (player.isOp()) {
            if (!(selectedEntity.equals(configName))) {
                loadConfigVariables(selectedEntity, false);
            }
        }
    }

    public void loadConfigVariables(String entity, Boolean onLoad) {

        plugin.oreGenConfig = plugin.oreGenConfigs.get(entity);
        getConfigName();
        getSelectedEntity();
        loadSpawnedOres();
        loadOreCollection();
        validateDefaultBlock();
        getRegenDuration();
        getRangeCoords();

        if (onLoad) {
            repairSpawnedOres();
        }
    }

    public <T> void saveConfig(String path, T val) {
        plugin.getConfig().set(path, val);
        plugin.saveConfig();
    }

    public List<String> getEntityList() {
        return new ArrayList<String>(
                plugin.getConfig().getConfigurationSection("entity-list").getValues(false).keySet());
    }

    public void setAllMinMaxEntities() {
        allEntitiesMinMax.clear();
        for (String entity : getEntityList()) {
            Integer[] xzMinMax = { plugin.getConfig().getInt("entity-list." + entity + ".coords.min.x"),
                    plugin.getConfig().getInt("entity-list." + entity + ".coords.max.x"),
                    plugin.getConfig().getInt("entity-list." + entity + ".coords.min.z"),
                    plugin.getConfig().getInt("entity-list." + entity + ".coords.max.z") };

            List<Integer> myList = Arrays.asList(xzMinMax);
            allEntitiesMinMax.put(entity, myList);
        }
    }

    public Integer getEntityMinMax(List<Integer> minmaxList, char coord, String minOrMax) {

        char x = 'x';
        char z = 'z';
        Integer index = -1;
        if (coord == x && minOrMax.equals("min")) {
            index = 0;
        } else if (coord == x && minOrMax.equals("max")) {
            index = 1;
        } else if (coord == z && minOrMax.equals("min")) {
            index = 2;
        } else if (coord == z && minOrMax.equals("max")) {
            index = 3;
        }

        return minmaxList.get(index);
    }

    private void getConfigName() {
        configName = plugin.oreGenConfig.getConfig().getString("general.name");
    }

    private void getSelectedEntity() {
        selectedEntity = plugin.getConfig().getString("selected-entity");
    }

    public void setSelectedEntity(String entity) {
        selectedEntity = entity;
        saveConfig("selected-entity", entity);
    }

    private void setRangeDistance(Integer length) {
        rangeDistance = length;
        saveConfig("entity-list." + selectedEntity + ".coords.distance", length);
    }

    private void getRegenDuration() {
        regenDuration = plugin.oreGenConfig.getConfig().getString("general.regen-duration");
    }

    public void setRegenDuration(String name) {
        regenDuration = name;
        plugin.oreGenConfig.set("general.regen-duration", name);
    }

    public void outLineCoords(final Player player, Integer length, String eventType, Boolean save) {

        Location entLoc = null;

        if (eventType.equals("set")) {
            entLoc = player.getLocation();
        } else if (eventType.equals("show") || eventType.equals("hide")) {
            if(plugin.getConfig().isSet("entity-list." + selectedEntity + ".coords.middle")){
                String savedLoc = plugin.getConfig().getString("entity-list." + selectedEntity + ".coords.middle");
                entLoc = OtherF.deserializeLocation(plugin, savedLoc);
            }
        }

        if(entLoc != null) {

            Location corner1 = new Location(entLoc.getWorld(), entLoc.getBlockX() + length, entLoc.getBlockY() + 1,
            entLoc.getBlockZ() - length);
            Location corner2 = new Location(entLoc.getWorld(), entLoc.getBlockX() - length, entLoc.getBlockY() + 1,
            entLoc.getBlockZ() + length);
            
        
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {

                if ((x == minX || x == maxX) || (z == minZ || z == maxZ)) {
                    final Block b = corner1.getWorld().getBlockAt(x, entLoc.getBlockY(), z);

                    if (eventType.equals("set")) {

                        player.sendBlockChange(b.getLocation(), Material.SEA_LANTERN.createBlockData()); // show outline

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                b.getState().update(); // undo to old block
                            }

                        }.runTaskLater(plugin, (20 * 5)); // seconds change the block to noprmal.
                    } else if (eventType.equals("show")) {
                        player.sendBlockChange(b.getLocation(), Material.SEA_LANTERN.createBlockData()); // show outline
                    } else if (eventType.equals("hide")) {
                        b.getState().update(); // undo to old block
                    }

                }
            }
        }

        if (eventType.equals("set")) {
            saveConfig("entity-list." + selectedEntity + ".coords.middle", OtherF.serializeLocation(entLoc.getBlock()));
            setRangeDistance(length);
            setRangeCoords(minX, maxX, minZ, maxZ);
        }
    }
    }

    private void getRangeCoords() {

        rangeCoords.clear();
        rangeDistance = plugin.getConfig().getInt("entity-list." + selectedEntity + ".coords.distance");

        rangeCoords.put("x-min", plugin.getConfig().getInt("entity-list." + selectedEntity + ".coords.min.x"));
        rangeCoords.put("x-max", plugin.getConfig().getInt("entity-list." + selectedEntity + ".coords.max.x"));
        rangeCoords.put("z-min", plugin.getConfig().getInt("entity-list." + selectedEntity + ".coords.min.z"));
        rangeCoords.put("z-max", plugin.getConfig().getInt("entity-list." + selectedEntity + ".coords.max.z"));

    }

    private void setRangeCoords(int minX, int maxX, int minZ, int maxZ) {

        rangeCoords.clear();

        saveConfig("entity-list." + selectedEntity + ".coords.min.x", minX);
        saveConfig("entity-list." + selectedEntity + ".coords.max.x", maxX);
        saveConfig("entity-list." + selectedEntity + ".coords.min.z", minZ);
        saveConfig("entity-list." + selectedEntity + ".coords.max.z", maxZ);

        rangeCoords.put("x-min", minX);
        rangeCoords.put("x-max", maxX);
        rangeCoords.put("z-min", minZ);
        rangeCoords.put("z-max", maxZ);

    }

    private void getDefaultBlock() {
        defaultBlock = plugin.oreGenConfig.getConfig().getString("general.default-block");
    }

    public void setDefaultBlock(String name) {
        defaultBlock = name;
        plugin.oreGenConfig.set("general.default-block", name);
    }

    public void getOreList(Player player) {

        Map<String, Object> oreList = getYamlMap("ore-spawn");
        player.sendMessage(prefix + "============= Ore List Size: [" + oreList.size() + "] =============");

        for (Map.Entry<String, Object> oreTypes : oreList.entrySet()) {
            String oreNames = oreTypes.getKey();
            Double oreValues = Double.parseDouble(oreTypes.getValue().toString());

            player.sendMessage(prefix + "Ore: " + oreNames + " ||==|| Spawn Rate: " + oreValues + "%");
        }

        player.sendMessage(prefix + "======================================");
    }

    // on plugin load, get default block and then validate if block is right.
    private void validateDefaultBlock() {
        getDefaultBlock();
        if (!OtherF.isMaterial(defaultBlock)) {
            setDefaultBlock("STONE");
        }
    }

    public Boolean isBlockMined(String blockLOC, String setDefaultBlock) {
        Location loc = OtherF.deserializeLocation(plugin, blockLOC);
        Block myCustomblock = loc.getBlock();
        String blockType = myCustomblock.getType().toString();

        return setDefaultBlock.equals(blockType);
    }

    public void removeBlockIG(String blockDetails) {
        Location loc = OtherF.deserializeLocation(plugin, blockDetails);
        Block myCustomblock = loc.getBlock();
        myCustomblock.setType(Material.AIR);
    }

    // get the block details associated to this block id.
    public String getSpawnedBlockDetails(String blockID) {
        return plugin.oreGenConfig.getConfig().getString("spawned-ores." + blockID);
    }

    public void removeBlock(String blockID) {
        removeBlockIG(getSpawnedBlockDetails(blockID));
        plugin.oreGenConfig.set("spawned-ores." + blockID, null); // remove block from the config
        loadSpawnedOres(); // reload spawned list after removing.
    }

    // loads all the ore names and percentages from yaml into Map
    public void loadOreCollection() {

        oreCollection.clear();
        Map<String, Object> oreList = getYamlMap("ore-spawn");

        for (Map.Entry<String, Object> entry : oreList.entrySet()) {
            String oreType = entry.getKey();
            Double orePercentage = Double.parseDouble(entry.getValue().toString()); // Very dodgy indeed
            oreCollection.add(orePercentage, oreType);
        }

    }

    // remove all spawned blocks and clear hashmap
    public void removeAllSpawnedOres() {
        plugin.oreGenConfig.getConfig().set("spawned-ores", null);
        plugin.oreGenConfig.saveConfig();
        spawnedList.clear();
    }

    // yaml object path
    public Map<String, Object> getYamlMap(String text) {
        return plugin.oreGenConfig.getConfig().getConfigurationSection(text).getValues(false);
    }

    // a nice little stream that gets all the blocks that are next in line to spawn.
    // this solves a big bug where config changes when you enter a different entity.
    private HashMap<String, Deque<List<String>>> oreRegenStream = new HashMap<String, Deque<List<String>>>();

    // regen ores by duration
    public void regenOre(final Block block, final String duration, String entity) {

        Integer seconds = OtherF.converDurationToSeconds(duration);
        List<String> regenDetails = new ArrayList<String>();
        regenDetails.add(oreCollection.next());
        regenDetails.add(seconds.toString());
        // if nothing in the list then add to parent hashmap else add to child hashmap
        if (!oreRegenStream.containsKey(entity)) {
            Deque<List<String>> regenDetailsList = new ArrayDeque<List<String>>();
            regenDetailsList.add(regenDetails);
            oreRegenStream.put(entity, regenDetailsList);
        } else {

            oreRegenStream.get(entity).add(regenDetails);
        }

        for (final Deque<List<String>> queryRegenDetailsList : oreRegenStream.values()) {

            if (queryRegenDetailsList.size() > 0) {

                final List<String> oreDetails = new ArrayList<String>(queryRegenDetailsList.getFirst());
                final String oreType = oreDetails.get(0);
                final Integer oreDuration = Integer.parseInt(oreDetails.get(1));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        block.setType(Material.matchMaterial(oreType));
                    }
                }.runTaskLater(plugin, (20 * oreDuration)); // seconds change the block

                queryRegenDetailsList.removeFirst();
            }
        }

    }

    // This method reloads all the objects from yaml file into Hashmap.
    private void loadSpawnedOres() {

        try {
            spawnedList.clear();

            Map<String, Object> customSpawnedOres = getYamlMap("spawned-ores");

            for (Map.Entry<String, Object> entry : customSpawnedOres.entrySet()) {
                String blockID = entry.getKey();
                String blockLOC = entry.getValue().toString();

                spawnedList.put(blockID, blockLOC);
            }

        } catch (Exception e) {

        }
    }

    // load each entity then reset blocks for those that werent respaawned
    public void repairSpawnedOres() {

        for (String entity : getEntityList()) {
            if (plugin.oreGenConfigs.get(entity).getConfig().isSet("spawned-ores")) {

                loadConfigVariables(entity, false);

                Map<String, Object> spawnedOres = plugin.oreGenConfigs.get(entity).getConfig()
                        .getConfigurationSection("spawned-ores").getValues(false);
                String configDefaultBlock = plugin.oreGenConfigs.get(entity).getConfig()
                        .getString("general.default-block");
                for (Object spawnedOresLocs : spawnedOres.values()) {

                    Location loc = OtherF.deserializeLocation(plugin, spawnedOresLocs.toString());
                    Block myCustomblock = loc.getBlock();

                    if (isBlockMined(spawnedOresLocs.toString(), configDefaultBlock)) {
                        myCustomblock.setType(Material.matchMaterial(oreCollection.next()));
                    }
                }
            }

        }
    }

}