package org.unikn.eurasim.servlets;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;

public class Configuration {

    public static double MOVEMENT_SPEED = 1.419;
    public static double DENSITY = 6.6;
    public static double TICK_SPEED = 1;

    public static String DB_DRIVER = "org.postgresql.Driver";
    public static String DB_HOST_URL= "jdbc:postgresql://localhost:5432/simevac";
    public static String DB_USER = "postgres";
    public static String DB_PASSWORD = "12345";

    public static Int2IntArrayMap EDGE_WIDTH_MAP;

    public static String NETLOGO_FILES_PATH = "/Users/thodoriscjn/Desktop/netlogo/";

    public static void load(String s) {
        // TODO: Set proper edge widths per class.
        EDGE_WIDTH_MAP = new Int2IntArrayMap();
        EDGE_WIDTH_MAP.put(15, 10);
        EDGE_WIDTH_MAP.put(16, 10);

        EDGE_WIDTH_MAP.put(21, 9);
        EDGE_WIDTH_MAP.put(22, 9);

        EDGE_WIDTH_MAP.put(31, 6);
        EDGE_WIDTH_MAP.put(32, 6);
        EDGE_WIDTH_MAP.put(41, 6);
        EDGE_WIDTH_MAP.put(42, 6);
        EDGE_WIDTH_MAP.put(43, 6);
        EDGE_WIDTH_MAP.put(51, 6);
        EDGE_WIDTH_MAP.put(62, 6);
        EDGE_WIDTH_MAP.put(63, 6);

        EDGE_WIDTH_MAP.put(71, 4);
        EDGE_WIDTH_MAP.put(72, 4);
        EDGE_WIDTH_MAP.put(91, 4);
        EDGE_WIDTH_MAP.put(92, 4);
    }
}
