
package com.troycardozo.myPlugin.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.troycardozo.myPlugin.App;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteract implements Listener {

    final App plugin;
    
    public PlayerInteract(App instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        ItemStack item = player.getInventory().getItemInMainHand();
     
        if (player.isOp() 
                && event.getAction() == Action.RIGHT_CLICK_BLOCK 
                    && event.getHand() == (EquipmentSlot.HAND)
                        && item.getType() == Material.STICK && item.hasItemMeta()) {


            String blockType = block.getType().toString();

            Map<String, Object> oreList = plugin.ore.getYamlMap("ore-spawn");
            List<String> ores = new ArrayList<String>(oreList.keySet());

            if (ores.contains(blockType)) {

                plugin.oreGenConfig.set("ore-spawn." + blockType, null);
                player.sendMessage(plugin.ore.prefix + "Block type removed from ore list: " + blockType);

            } else {

                plugin.oreGenConfig.set("ore-spawn." + blockType, 0);
                player.sendMessage(plugin.ore.prefix + "Block type added to ore list: " + blockType);
                player.sendMessage(plugin.ore.prefix
                        + "The spawn rate is set to 0%. Change this by holding the block in your inventory and set the % like `/ore config percent 20`");
            }

            plugin.ore.loadOreCollection();
            plugin.ore.getOreList(player);
        }

    }
}
