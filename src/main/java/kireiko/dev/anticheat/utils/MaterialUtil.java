package kireiko.dev.anticheat.utils;

import net.minestom.server.item.Material;

public final class MaterialUtil {

    public static Material getMaterial(String modernName, String legacyName) {
        try {
            return Material.fromKey(modernName.toLowerCase());
        } catch (IllegalArgumentException a) {
            try {
                return Material.fromKey(legacyName.toLowerCase());
            } catch (IllegalArgumentException b) {
                return Material.STONE;
            }
        }
    }
}
