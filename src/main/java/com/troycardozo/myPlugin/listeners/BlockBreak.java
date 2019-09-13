package com.troycardozo.myPlugin.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.troycardozo.myPlugin.App;
import com.troycardozo.myPlugin.Utils.OtherF;

public class BlockBreak implements Listener {

    final App plugin;

    public BlockBreak(App instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        Integer blockX = (int) block.getX();
        Integer blockZ = (int) block.getZ();

        for (Map.Entry<String, List<Integer>> allEntitiesminMaxentry : plugin.ore.allEntitiesMinMax.entrySet()) {
            String entityID = allEntitiesminMaxentry.getKey();
            List<Integer> entityMinMax = allEntitiesminMaxentry.getValue();

            Integer x_min = plugin.ore.getEntityMinMax(entityMinMax, 'x', "min");
            Integer x_max = plugin.ore.getEntityMinMax(entityMinMax, 'x', "max");
            Integer z_min = plugin.ore.getEntityMinMax(entityMinMax, 'z', "min");
            Integer z_max = plugin.ore.getEntityMinMax(entityMinMax, 'z', "max");

            if ((blockX > x_min && blockX < x_max) && (blockZ > z_min && blockZ < z_max)) {
                String blockLOC = OtherF.serializeLocation(block);

                // opped with creative side part
                // ========================================================================================================================
                if (player.isOp() && player.getGameMode() == GameMode.CREATIVE) {

                    if (entityID.equals(plugin.ore.selectedEntity)) {

                        for (Map.Entry<String, String> blockEntry : plugin.ore.spawnedList.entrySet()) {
                            String blockID = blockEntry.getKey();
                            String blockLocation = blockEntry.getValue();

                            if (blockLOC.equals(blockLocation)) {
                                plugin.ore.spawnedList.remove(blockID);
                                plugin.oreGenConfig.set("spawned-ores." + blockID, null);
                                return;
                            }
                        }

                    } else {
                        // access other configs temporarily to get the block locs.
                        Map<String, Object> otherBlockLocs = plugin.oreGenConfigs.get(entityID).getConfig()
                                .getConfigurationSection("spawned-ores").getValues(false);

                        for (Object otherBlockLocsEntry : otherBlockLocs.values()) {
                            String otherblockLOC = otherBlockLocsEntry.toString();

                            if (blockLOC.equals(otherblockLOC)) {
                                player.sendMessage(plugin.ore.prefix + ChatColor.RED
                                        + "Cant break this block! Make sure you have the correct entity selected.");
                                event.setCancelled(true); // this makes it so you cant mine the default blocks.
                                return;
                            }
                        }

                    }
                    return;
                }

                // survival and client side part.
                // ========================================================================================================================

                if (!entityID.equals(plugin.ore.configName)) {
                    plugin.ore.loadConfigVariables(entityID, false);
                }

                List<String> blockLOCList = new ArrayList<String>(plugin.ore.spawnedList.values());

                if (blockLOCList.contains(blockLOC)) {
                    if (!plugin.ore.isBlockMined(blockLOC, plugin.ore.defaultBlock)) {

                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                Block airBlock = block.getLocation().getBlock();
                                airBlock.setType(Material.matchMaterial(plugin.ore.defaultBlock));
                            }

                        }.runTaskLater(plugin, 1); // 1 tick

                        plugin.ore.regenOre(block, plugin.ore.regenDuration, entityID);

                    } else {
                        player.sendMessage(plugin.ore.prefix + ChatColor.RED + "Wait till the ore regens!");
                        event.setCancelled(true); // this makes it so you cant mine the default blocks.
                    }
                }
            }
        }
    }

}