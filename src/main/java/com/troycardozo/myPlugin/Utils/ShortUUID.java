package com.troycardozo.myPlugin.Utils;

import java.util.Random;

/******
 * 
 * Short human readable id generator
 * https://stackoverflow.com/questions/9543715/generating-human-readable-usable-short-but-unique-ids
 */

public class ShortUUID {
    private static char[] _base62chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    private static Random _random = new Random();

    public static String GetBase62(int length) {
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++)
            sb.append(_base62chars[_random.nextInt(62)]);

        return sb.toString();
    }

    public static String GetBase36(int length) {
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++)
            sb.append(_base62chars[_random.nextInt(36)]);

        return sb.toString();
    }
}