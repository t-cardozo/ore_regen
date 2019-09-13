package com.troycardozo.myPlugin.Utils;

import com.troycardozo.myPlugin.App;

import org.bukkit.block.Block;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;;

/*
*   My functions 
*/
public class OtherF {
    public static Integer generateRand(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    public static String serializeLocation(Block block) {
        String locWorld = block.getLocation().getWorld().getName();
        String xCoord = Double.toString(block.getLocation().getX());
        String yCoord = Double.toString(block.getLocation().getY());
        String zCoord = Double.toString(block.getLocation().getZ());

        String blockCoords = locWorld + ":" + xCoord + ":" + yCoord + ":" + zCoord;
        return blockCoords;
    }

    public static Location deserializeLocation(App plugin, String loc) {
        String[] blockLocation = loc.split(":"); // get location
        World bWorld = plugin.getServer().getWorld(blockLocation[0]);
        Double x = Double.parseDouble(blockLocation[1]);
        Double y = Double.parseDouble(blockLocation[2]);
        Double z = Double.parseDouble(blockLocation[3]);

        return new Location(bWorld, x, y, z);
    }

    public static Boolean isMaterial(String name) {
        return Material.getMaterial(name) != null;
    }

    public static void log(String name) {
         Bukkit.getConsoleSender().sendMessage(name);
    }

    public static long convertToSeconds(int value, char unit) {
        long ret = value;
        switch (unit) {
        case 'd':
            ret = TimeUnit.DAYS.toSeconds(value);
            break;
        case 'h':
            ret = TimeUnit.HOURS.toSeconds(value);
            break;
        case 'm':
            ret = TimeUnit.MINUTES.toSeconds(value);
            break;
        case 's':
            break;
        default:
            // fail
            break;
        }
        return ret;
    }

    public static int converDurationToSeconds(String duration) {

        int seconds = 0;
        String[] durationList = duration.split("\\s+"); // split on space

        for (String durationType : durationList) {

            int t = Integer.parseInt(durationType.replaceAll("\\D+", ""));
            char unit = durationType.replaceAll("[^a-zA-Z]+", "").charAt(0);
            seconds += (int) OtherF.convertToSeconds(t, unit);
        }

        return seconds;
    }

    public static String concatArgsTime(String[] args) {
        String concatArgs = "";
        for (int i = 0; i < args.length; i++) {
            if (i == 0 || i == 1)
                continue;
            concatArgs += ((concatArgs.length() == 0) ? "" : " ") + args[i];
        }
        return concatArgs;
    }

}