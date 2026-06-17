package kireiko.dev.anticheat.utils.protocol;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public final class ProtocolTools {

    public static boolean invalidTeleport(Pos location) {
        return location == null
                || location.x() == 8.5
                || location.z() == 8.5;
    }

    public static boolean isLoadLocation(Pos location) {
        return (location.x() == 1 && location.y() == 1 && location.z() == 1);
    }

    public static Pos getLoadLocation() {
        return new Pos(1, 1, 1);
    }

    public static Block getBlockAsync(Instance instance, int x, int y, int z) {
        if (instance != null && instance.isChunkLoaded(x >> 4, z >> 4)) {
            return instance.getBlock(x, y, z);
        }
        return null;
    }
}
