package com.troycardozo.myPlugin.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.troycardozo.myPlugin.App;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class OreTabCompleter implements TabCompleter {

    final App plugin;
    private static final List<String> MAIN_COMMANDS = new ArrayList<String>(
            Arrays.asList("entity", "config", "tools", "reload"));

    private static final List<String> ENTITY_COMMANDS = new ArrayList<String>(
            Arrays.asList("create", "delete", "set", "list", "coords", "clearall"));

    private static final List<String> CONFIG_COMMANDS = new ArrayList<String>(
            Arrays.asList("percent", "types", "duration", "defaultblock", "placedblocks"));

    private static final List<String> COORDS_COMMANDS = new ArrayList<String>(
            Arrays.asList("set", "tp", "show", "hide"));

    // create a static array of values
    public OreTabCompleter(App instance) {
        plugin = instance;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        final List<String> completions = new ArrayList<>();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.isOp()) {

                if (args.length == 1) {
                    StringUtil.copyPartialMatches(args[0], MAIN_COMMANDS, completions);
                    Collections.sort(completions);
                    return completions;
                }

                if (args.length == 2) {

                    if (args[0].equals("entity")) {
                        StringUtil.copyPartialMatches(args[1], ENTITY_COMMANDS, completions);
                        Collections.sort(completions);
                        return completions;
                    }

                    if (args[0].equals("config")) {
                        StringUtil.copyPartialMatches(args[1], CONFIG_COMMANDS, completions);
                        Collections.sort(completions);
                        return completions;
                    }
                }

                if (args.length == 3) {
                    if (args[1].equals("coords")) {
                        StringUtil.copyPartialMatches(args[2], COORDS_COMMANDS, completions);
                        Collections.sort(completions);
                        return completions;
                    }
                }
            }

        }

        return completions;
    }

}