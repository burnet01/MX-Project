package kireiko.dev.anticheat.services;

import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.core.AsyncScheduler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.time.TimeUnit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public final class SimulationFlagService {

    @Getter
    private static final List<Flag> flags = new CopyOnWriteArrayList<>();

    public static void init() {
        MinecraftServer.getSchedulerManager().buildTask(() -> {
            AsyncScheduler.run(() -> {
                final Set<Flag> toRemove = new HashSet<>();
                for (Flag flag : flags) {
                    flag.location = flag.location.add(flag.vector);
                    if (!isPointWall(flag.getLocation(), 0.3)) {
                        final Pos finalLoc = flag.getLocation();
                        MinecraftServer.getSchedulerManager().buildTask(() -> {
                            flag.getProfile().getPlayer().teleport(finalLoc);
                        }).schedule();
                        flag.setVector(new Vec(
                                flag.vector.x() * 0.91,
                                flag.vector.y() - (0.08 * 0.98),
                                flag.vector.z() * 0.91));
                    } else toRemove.add(flag);
                }
                flags.removeAll(toRemove);
            });
        }).repeat(1, TimeUnit.SERVER_TICK).schedule();
    }

    private static boolean isPointWall(Pos location, final double scale) {
        final double x = location.x();
        final double y = location.y() + 0.1;
        final double z = location.z();
        Instance instance = MinecraftServer.getInstanceManager().getInstances().stream().findFirst().orElse(null);
        if (instance == null) return false;

        for (int dx = -1; dx <= 1; ++dx) {
            for (int dy = -1; dy <= 1; ++dy) {
                for (int dz = -1; dz <= 1; ++dz) {
                    final Block block = instance.getBlock(
                            (int) (x + (double) dx * scale),
                            (int) (y + (double) dy * scale),
                            (int) (z + (double) dz * scale)
                    );

                    if (!block.compare(Block.AIR)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Data
    @AllArgsConstructor
    public static class Flag {
        private final PlayerProfile profile;
        private Pos location;
        private Vec vector;
    }
}
