package com.troycardozo.myPlugin.listeners;

import com.troycardozo.myPlugin.App;
import com.troycardozo.myPlugin.Utils.OtherF;
import com.troycardozo.myPlugin.Utils.ShortUUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class BlockPlaced implements Listener {

    final App plugin;

    public BlockPlaced(App instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();
        final Block block = event.getBlockPlaced();

        if (player.isOp() && block.getType() == Material.BEDROCK && item.hasItemMeta()) {
            Location blockLocation = block.getLocation();
            Integer blockX = (int) blockLocation.getX();
            Integer blockZ = (int) blockLocation.getZ();

            plugin.ore.entityConfigMatch(player);

            if ((blockX > plugin.ore.rangeCoords.get("x-min") && blockX < plugin.ore.rangeCoords.get("x-max"))
                    && (blockZ > plugin.ore.rangeCoords.get("z-min") && blockZ < plugin.ore.rangeCoords.get("z-max"))) {

                final String blockID = ShortUUID.GetBase36(5);
                final String bLoc = OtherF.serializeLocation(block);

                block.setType(Material.matchMaterial(plugin.ore.defaultBlock));

                plugin.oreGenConfig.set("spawned-ores." + blockID, bLoc);
                plugin.ore.spawnedList.put(blockID, bLoc); // save location to hashmap

                plugin.ore.regenOre(block, "1s", plugin.ore.selectedEntity);

            } else {
                event.setCancelled(true);
                player.sendMessage(plugin.ore.prefix + ChatColor.RED
                        + "You are trying to place this outside the selected range, do `/ore entity coords tp` or `/ore entity coords show`");
            }

        }
    }
}
