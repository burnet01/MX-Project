package kireiko.dev.anticheat.utils.protocol;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;

import java.util.UUID;

public final class MinestomUtil {

    public static UUID getUUID(Entity entity) {
        if (entity instanceof Player player) {
            return player.getUuid();
        }
        return entity.getUuid();
    }

    public static int getEntityID(Entity entity) {
        return entity.getEntityId();
    }

    public static Pos getLocationOrNull(Entity entity) {
        if (entity instanceof Player player) {
            return player.getPosition();
        }
        return entity.getPosition();
    }

    public static Player getPlayerFromEntity(Entity entity) {
        if (entity instanceof Player player) {
            return player;
        }
        return null;
    }
}
