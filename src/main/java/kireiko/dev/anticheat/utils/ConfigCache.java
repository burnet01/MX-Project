package kireiko.dev.anticheat.utils;

import kireiko.dev.anticheat.MX;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public final class ConfigCache {

    public static double VL_LIMIT;
    public static float VL_RESET;
    public static String ALERT_MSG;
    public static String UNUSUAL;
    public static String SUSPECTED;
    public static String BAN_COMMAND;
    public static String BYPASS;
    public static String BC_MSG;
    public static boolean PUNISH_EFFECT;
    public static boolean INTERACT_SPELL;
    public static boolean IGNORE_CINEMATIC;
    public static boolean LOG_IN_FILES;
    public static boolean ROTATIONS_CONTAINER;
    public static boolean PREVENT_GHOST_BLOCK_ABUSE;
    public static int PREVENTION;

    private static final Map<String, String> configMap = new HashMap<>();

    public static void loadConfig() {
        loadDefaultConfig();
        VL_LIMIT = getDouble("vlLimit", 100);
        VL_RESET = (float) getDouble("vlReset", 15);
        PREVENTION = getInt("prevention", 2);
        ALERT_MSG = getString("alertMsg", "&9&l[MX] &e%player% &8>>&c %check% &7(&c%component%&7) &8%info% &f[%vl%/%vlLimit%]");
        UNUSUAL = getString("unusual", "&9&l[MX] &e%player% &8>>&6 Playing suspiciously");
        SUSPECTED = getString("suspected", "&9&l[MX] &e%player% &8>>&4 Looks like a cheater!");
        BAN_COMMAND = getString("banCommand", "ban %player% 1d Unfair advantage");
        BYPASS = getString("bypass", "mx.bypass");
        BC_MSG = getString("bcMsg", "&c&l[MX]&f %message%");
        PUNISH_EFFECT = getBoolean("punishEffect", false);
        INTERACT_SPELL = getBoolean("interactSpell", false);
        IGNORE_CINEMATIC = getBoolean("ignoreCinematic", false);
        LOG_IN_FILES = getBoolean("logInFiles", true);
        ROTATIONS_CONTAINER = getBoolean("rotationsContainer", true);
        PREVENT_GHOST_BLOCK_ABUSE = getBoolean("preventGhostBlockAbuse", false);
    }

    private static void loadDefaultConfig() {
        configMap.clear();
        File configFile = new File(MX.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            MX.getDataFolder().mkdirs();
            try (InputStream is = MX.class.getResourceAsStream("/config.yml")) {
                if (is != null) {
                    try (OutputStream os = new FileOutputStream(configFile)) {
                        is.transferTo(os);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int colonIndex = line.indexOf(':');
                if (colonIndex > 0) {
                    String key = line.substring(0, colonIndex).trim();
                    String value = line.substring(colonIndex + 1).trim();
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    } else if (value.startsWith("'") && value.endsWith("'")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    configMap.put(key, value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getString(String key, String defaultValue) {
        return configMap.getOrDefault(key, defaultValue);
    }

    private static double getDouble(String key, double defaultValue) {
        String value = configMap.get(key);
        if (value != null) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private static int getInt(String key, int defaultValue) {
        String value = configMap.get(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private static boolean getBoolean(String key, boolean defaultValue) {
        String value = configMap.get(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }
}
