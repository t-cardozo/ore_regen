package com.troycardozo.myPlugin.commands;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.troycardozo.myPlugin.App;
import com.troycardozo.myPlugin.Docs;
import com.troycardozo.myPlugin.Utils.OtherF;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class OreCommand implements CommandExecutor {

    final App plugin;

    public OreCommand(App instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // This if will only work if players type this out, so no command blocks etc.
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.isOp()) {

                plugin.ore.entityConfigMatch(player);
                if (args.length == 0) {
                    player.sendMessage(Docs.MAIN);
                    player.sendMessage(plugin.ore.prefix + "Usage: /" + label + " <key> <value>");
                    return false;
                }
                switch (args[0]) {
                case "help":
                    player.sendMessage(Docs.MAIN);
                    break;
                case "reload":
                    plugin.ore.repairSpawnedOres();
                    break;
                case "tools":
                    givePlayerTool(player);
                    break;
                case "config":
                    entityConfig(player, args);
                    break;
                case "entity":
                    oreEntity(player, args);
                    break;
                default:
                    player.sendMessage(plugin.ore.prefix + "Invalid command");
                }
            } else {
                player.sendMessage(plugin.ore.prefix + "Insufficent permissions!");
            }
        }

        return false;
    }

    private void oreEntity(final Player player, String[] args) {

        if (args.length == 1) {
            player.sendMessage(plugin.ore.prefix + "Entity commands: ");
            player.sendMessage(Docs.ENTITY_DOCS);
            player.sendMessage(plugin.ore.prefix + "Entity currently selected is: " + plugin.ore.selectedEntity);
            return;
        }

        switch (args[1]) {
        case "create":
            createEntity(player, args);
            break;
        case "list":
            plugin.ore.displayEntityList(player);
            break;
        case "delete":
            deleteEntity(player, args);
            break;
        case "set":
            setEntity(player, args);
            break;
        case "coords":
            entityCoords(player, args);
            break;
        case "clearall":
            removeAllOres(player);
            break;
        default:
            player.sendMessage(plugin.ore.prefix + "Invalid command");
        }
    }

    private void createEntity(Player player, String[] args) {

        if (args.length == 2) {
            player.sendMessage(plugin.ore.prefix + "Invalid Entity name!");
            return;
        }
        String entity = args[2];
        plugin.ore.addEntitytoList(entity);

        File saveFile = new File(plugin.getDataFolder(), entity + plugin.configFileName);
        Reader loadFile = new InputStreamReader(plugin.getResource("default-oreGenConfig.yml"));
        YamlConfiguration loadDefaultconfig = YamlConfiguration.loadConfiguration(loadFile);

        try {
            loadDefaultconfig.save(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        plugin.ore.highlightAllSpawnedBlocks(false, player);
        plugin.ore.outLineCoords(player, plugin.ore.rangeDistance, "hide", false); // hide any previous outlines

        plugin.ore.loadConfigs();
        plugin.ore.setSelectedEntity(entity);
        plugin.ore.loadConfigVariables(entity, false);

        player.sendMessage(plugin.ore.prefix
                + "Entity added to the list. Start by adding a range by doing `/ore entity coords set 5`");

        plugin.ore.displayEntityList(player);
    }

    private void setEntity(Player player, String[] args) {
        if (args.length == 2) {
            player.sendMessage(plugin.ore.prefix + "Invalid Entity name!");
            return;
        }
        String entity = args[2];
        if (plugin.ore.getEntityList().contains(entity)) {
            plugin.ore.highlightAllSpawnedBlocks(false, player);
            plugin.ore.outLineCoords(player, plugin.ore.rangeDistance, "hide", false); // hide any previous outlines
            plugin.ore.setSelectedEntity(entity);
            plugin.ore.loadConfigVariables(entity, false);
            player.sendMessage(plugin.ore.prefix + "Entity successfully changed to " + entity);
            plugin.ore.displayEntityList(player);
        } else {
            plugin.ore.displayEntityList(player);
            player.sendMessage(plugin.ore.prefix + "Entity doesn't exist, try again!");
        }
    }

    private void deleteEntity(Player player, String[] args) {

        String entity = plugin.ore.selectedEntity;
        String firstEntity = plugin.ore.getEntityList().get(0);

        removeAllOres(player);
        plugin.ore.outLineCoords(player, plugin.ore.rangeDistance, "hide", false); // hide any previous outlines
        plugin.ore.setSelectedEntity(firstEntity);
        plugin.ore.loadConfigVariables(firstEntity, false);

        plugin.ore.saveConfig("entity-list." + entity, null);
        File deleteFile = new File(plugin.getDataFolder(), entity + plugin.configFileName);
        deleteFile.delete();
        player.sendMessage(plugin.ore.prefix + "Entity: " + entity + " has been deleted successfully!");
        player.sendMessage(plugin.ore.prefix + "Entity: " + firstEntity + " is now selected!");
    }

    private void entityCoords(Player player, String[] args) {

        if (args.length == 2) {
            player.sendMessage(plugin.ore.prefix + "Entity coords commands: ");
            player.sendMessage(Docs.ENTITY_COORDS_DOCS);
            return;
        }

        if (args[2].equals("set")) {
            setEntityCoords(player, args);
            return;
        }

        if (!plugin.getConfig().isSet("entity-list." + plugin.ore.selectedEntity + ".coords.distance")) {
            player.sendMessage(plugin.ore.prefix
                    + "You need to set a spawn range before using the following commands.. `/ore entity coords set 50`");
            return;
        }

        switch (args[2]) {
        case "tp":
            tpEntityCoords(player);
            break;
        case "show":
            plugin.ore.outLineCoords(player, plugin.ore.rangeDistance, "show", false);
            break;
        case "hide":
            plugin.ore.outLineCoords(player, plugin.ore.rangeDistance, "hide", false);
            break;
        default:
            player.sendMessage(plugin.ore.prefix + "Invalid command");
        }
    }

    // set a range to the associated entity
    private void setEntityCoords(Player player, String[] args) {

        if (args.length == 3) {
            player.sendMessage(plugin.ore.prefix + "Enter a range eg 5");
            return;
        }
        Integer range = Integer.parseInt(args[3]);
        plugin.ore.outLineCoords(player, range, "set", true);
        plugin.ore.setAllMinMaxEntities();
        player.sendMessage(plugin.ore.prefix + "Range has been set.");
    }

    // tps to the center of the entity coords
    private void tpEntityCoords(Player player) {
        player.sendMessage(plugin.ore.prefix + "Teleporting to the set ore spawn coords");
        String savedLoc = plugin.getConfig().getString("entity-list." + plugin.ore.selectedEntity + ".coords.middle");
        player.teleport(OtherF.deserializeLocation(plugin, savedLoc));
        plugin.ore.outLineCoords(player, plugin.ore.rangeDistance, "set", false);
    }

    private void entityConfig(Player player, String[] args) {

        if (args.length == 1) {
            player.sendMessage(plugin.ore.prefix + "Config commands: ");
            player.sendMessage(Docs.CONFIG_DOCS);
            return;
        }
        switch (args[1]) {
        case "percent":
            entityPercentConfig(player, args);
            break;
        case "types":
            plugin.ore.getOreList(player);
            break;
        case "duration":
            entityDurationConfig(player, args);
            break;
        case "defaultblock":
            entityDefaultBlockConfig(player, args);
            break;
        case "placedblocks":
            entityPlacedBlocksConfig(player);
            break;
        default:
            player.sendMessage(plugin.ore.prefix + "Invalid command");
        }
    }

    private void entityPlacedBlocksConfig(Player player) {

        if (plugin.ore.togglePlacedBlocksConfig) {

            if (plugin.ore.spawnedList.size() > 0) {
                player.sendMessage(plugin.ore.prefix + "Spawned Ore Blocks: [" + plugin.ore.spawnedList.size() + "]");
            } else {
                player.sendMessage(plugin.ore.prefix + "No spawned blocks!");
            }

            plugin.ore.highlightAllSpawnedBlocks(true, player);
        } else {
            plugin.ore.highlightAllSpawnedBlocks(false, player);
        }
    }

    // command to set ore percentage by getting block in hand
    private void entityPercentConfig(Player player, String[] args) {
        if (args.length == 2) {
            plugin.ore.getOreList(player);
            player.sendMessage(plugin.ore.prefix + "Enter a range eg 5");
            return;
        }

        Material blockType = player.getInventory().getItemInMainHand().getType();
        Map<String, Object> oreList = plugin.ore.getYamlMap("ore-spawn");
        List<String> ores = new ArrayList<String>(oreList.keySet());

        if (ores.contains(blockType.toString())) {

            Double percent = Double.parseDouble(args[2]);
            plugin.oreGenConfig.set("ore-spawn." + blockType, percent);
            plugin.ore.loadOreCollection();
            player.sendMessage(plugin.ore.prefix + percent + "% added to " + blockType);
            plugin.ore.getOreList(player);

        } else {
            player.sendMessage(plugin.ore.prefix
                    + "The block isn't in the ore type list. Check `/ore entity config types` to find what's in the list.");
        }

    }

    // set default block of what it will show when ore is mined.
    private void entityDefaultBlockConfig(Player player, String[] args) {

        if (args.length == 2) {
            player.sendMessage(plugin.ore.prefix + "Current default block is: " + plugin.ore.defaultBlock);
            player.sendMessage(plugin.ore.prefix + "Set a duration like this `/ore config defaultblock bookshelf`");
            return;
        }

        if (OtherF.isMaterial(args[2].toUpperCase())) {

            plugin.ore.setDefaultBlock(args[2].toUpperCase());
            player.sendMessage(plugin.ore.prefix + "Default block is now set to: " + args[2]);
        } else {
            player.sendMessage(plugin.ore.prefix + "Default block name is invalid! Try again.");
        }
    }

    // set duration to how long it takes for block to regen.
    private void entityDurationConfig(Player player, String[] args) {

        if (args.length == 2) {
            player.sendMessage(plugin.ore.prefix + "Current Ore duration is: " + plugin.ore.regenDuration);
            player.sendMessage(plugin.ore.prefix + "Set a duration like this `/ore config duration 1m 5s`");
            return;
        }

        String duration = OtherF.concatArgsTime(args);
        plugin.ore.setRegenDuration(duration);
        player.sendMessage(plugin.ore.prefix + "Ore regen duration successfully added!");
    }

    // clear all ores that were spawned.
    private void removeAllOres(Player player) {

        for (String customOreBlock : plugin.ore.spawnedList.values()) {
            plugin.ore.removeBlockIG(customOreBlock);
        }

        Integer size = plugin.ore.spawnedList.size();
        plugin.ore.removeAllSpawnedOres();
        player.sendMessage(
                plugin.ore.prefix + "All placed ore blocks are now removed for this entity. Blocks: " + size);
    }

    // give player all the tools to place and customise ores.
    private void givePlayerTool(Player player) {
        ItemStack oreGenerator = new ItemStack(Material.BEDROCK, 1);
        ItemMeta oreGeneratorMeta = oreGenerator.getItemMeta();
        oreGeneratorMeta.addEnchant(Enchantment.ARROW_DAMAGE, 10, true);
        oreGeneratorMeta.setDisplayName(ChatColor.UNDERLINE + "Ore Placer");
        oreGeneratorMeta.setLore(Arrays.asList(
                "Place this block in the coordinates assigned to the entity, to set this, do `/ore entity coords set 2`",
                "Change the default block by doing `/ore config defaultblock stone`",
                "Change the duration of regen by doing `/ore config duration 1m 10s`"));
        oreGenerator.setItemMeta(oreGeneratorMeta);

        ItemStack oreCustomiser = new ItemStack(Material.STICK, 1);
        ItemMeta oreCustomiserMeta = oreCustomiser.getItemMeta();
        oreCustomiserMeta.addEnchant(Enchantment.ARROW_DAMAGE, 10, true);
        oreCustomiserMeta.setDisplayName(ChatColor.UNDERLINE + "Ore Customiser");
        oreCustomiserMeta.setLore(Arrays.asList("Right click the block with this stick to toggle in the list.",
                "If you add the block to the list, hold that block in your hand then set a percentage to it like `/ore config percent 50`"));
        oreCustomiser.setItemMeta(oreCustomiserMeta);

        player.getInventory().addItem(oreGenerator, oreCustomiser);
        player.sendMessage(plugin.ore.prefix + "Ore plugin tools added to your inventory!");
    }

}
