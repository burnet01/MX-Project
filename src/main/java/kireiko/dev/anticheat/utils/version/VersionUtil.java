package kireiko.dev.anticheat.utils.version;

import lombok.Getter;
import net.minestom.server.MinecraftServer;

public final class VersionUtil {

    @Getter
    private static final String version = MinecraftServer.VERSION_NAME;

    public static boolean is1_8() {
        return version.startsWith("1.8");
    }

    public static boolean is1_8orAbove() {
        return compareVersion("1.8") >= 0;
    }

    public static boolean is1_9orAbove() {
        return compareVersion("1.9") >= 0;
    }

    public static boolean is1_12orAbove() {
        return compareVersion("1.12") >= 0;
    }

    public static boolean is1_13orAbove() {
        return compareVersion("1.13") >= 0;
    }

    public static boolean is1_14orAbove() {
        return compareVersion("1.14") >= 0;
    }

    public static boolean is1_15orAbove() {
        return compareVersion("1.15") >= 0;
    }

    public static boolean is1_16orAbove() {
        return compareVersion("1.16") >= 0;
    }

    public static boolean is1_17orAbove() {
        return compareVersion("1.17") >= 0;
    }

    public static boolean is1_18orAbove() {
        return compareVersion("1.18") >= 0;
    }

    public static boolean is1_19orAbove() {
        return compareVersion("1.19") >= 0;
    }

    public static boolean is1_20orAbove() {
        return compareVersion("1.20") >= 0;
    }

    public static boolean is1_21orAbove() {
        return compareVersion("1.21") >= 0;
    }

    private static int compareVersion(String v2) {
        String[] v1Parts = VersionUtil.version.split("\\.");
        String[] v2Parts = v2.split("\\.");

        int len = Math.max(v1Parts.length, v2Parts.length);
        for (int i = 0; i < len; i++) {
            int v1Part = i < v1Parts.length ? Integer.parseInt(v1Parts[i]) : 0;
            int v2Part = i < v2Parts.length ? Integer.parseInt(v2Parts[i]) : 0;

            if (v1Part != v2Part) {
                return v1Part - v2Part;
            }
        }

        return 0;
    }
}
