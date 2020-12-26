package com.bobo.reves.mysql.utils;

public class StringUtils {
    public static boolean isEmpty(String str) {
        return !(str != null && str != "" && !str.equals("null") && str.length() > 0);
    }

    public static String repeat(String str, int num) {
        return String.format("%0" + num + "d", 0).replace("0", str);
    }


}
