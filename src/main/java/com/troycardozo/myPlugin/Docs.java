package com.troycardozo.myPlugin;

import org.bukkit.ChatColor;

public class Docs {

        // horrible code i know.
        final public static String[] ENTITY_COORDS_DOCS = {
                        ChatColor.AQUA + "      /ore entity coords set <value>" + ChatColor.GREEN
                                        + " #creates a spawn range, to place your special blocks.",
                        ChatColor.AQUA + "      /ore entity coords tp" + ChatColor.GREEN
                                        + " #teleports you to the center of the spawn range",
                        ChatColor.AQUA + "      /ore entity coords show" + ChatColor.GREEN
                                        + " #Shows outline of spawn range",
                        ChatColor.AQUA + "      /ore entity coords hide" + ChatColor.GREEN
                                        + " #Hides outline of spawn range." };

        final public static String[] ENTITY_DOCS = {
                        ChatColor.AQUA + "  /ore entity create" + ChatColor.GREEN
                                        + " #create entity <name>, creates a new config with default values!",
                        ChatColor.AQUA + "  /ore entity delete" + ChatColor.GREEN
                                        + " #delete entity, removes config and selects first entity in list",
                        ChatColor.AQUA + "  /ore entity list" + ChatColor.GREEN
                                        + " #Shows full list of entities and highlights the selected one.",
                        ChatColor.AQUA + "  /ore entity set <name>" + ChatColor.GREEN + " #Select a different entity.",
                        ChatColor.AQUA + "  /ore entity coords <name>" + ChatColor.GREEN
                                        + " #Select a different entity.",
                        ChatColor.AQUA + "  /ore entity clearall" + ChatColor.GREEN
                                        + " #Removes all blocks that belong to the selected entity." };

        final public static String[] CONFIG_DOCS = {
                        ChatColor.AQUA + "  /ore config percent <value>" + ChatColor.GREEN
                                        + " #hold the block and set a percentage [1-100] can even go with decimals.",
                        ChatColor.AQUA + "  /ore config duration <value>" + ChatColor.GREEN
                                        + " #change the duration using simple time like 10m 20s!",
                        ChatColor.AQUA + "  /ore config defaultblock <name>" + ChatColor.GREEN
                                        + " #change the default block that shows when ore is mined, google the material names to set the name.",
                        ChatColor.AQUA + "  /ore config types" + ChatColor.GREEN + " #block types in the list!",
                        ChatColor.AQUA + "  /ore config placedblocks" + ChatColor.GREEN
                                        + " #get a total amount of blocks placed for this entity!",

        };

        final public static String[] MAIN = { ChatColor.YELLOW + "OreRegen Mod || version 0.1 || t_r_o_y_c_a_r_z",
                        ChatColor.YELLOW + "======================================", ChatColor.DARK_RED + "Docs: ",
                        ChatColor.AQUA + "/ore tools" + ChatColor.GREEN
                                        + " #Gives a ore placer & customiser, description on tools!",
                        ChatColor.AQUA + "/ore entity" + ChatColor.GREEN
                                        + " #create/delete/select entity with its own configs. Create multiple different caves, gardens etc!",
                        ENTITY_DOCS[0], ENTITY_DOCS[1], ENTITY_DOCS[2], ENTITY_DOCS[3], ENTITY_DOCS[4],
                        ENTITY_COORDS_DOCS[0], ENTITY_COORDS_DOCS[1], ENTITY_COORDS_DOCS[2], ENTITY_COORDS_DOCS[3],
                        ENTITY_DOCS[5],
                        ChatColor.AQUA + "/ore config" + ChatColor.GREEN + " #Configure settings for entity",
                        CONFIG_DOCS[0], CONFIG_DOCS[1], CONFIG_DOCS[2], CONFIG_DOCS[3], CONFIG_DOCS[4],
                        ChatColor.AQUA + "/ore reload" + ChatColor.GREEN
                                        + " #Reload all the ores for all entities (useful if you made a mistake with duration)" };

}