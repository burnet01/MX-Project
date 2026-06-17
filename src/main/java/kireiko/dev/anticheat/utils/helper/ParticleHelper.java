package kireiko.dev.anticheat.utils.helper;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.particle.Particle;
import net.minestom.server.network.packet.server.play.ParticlePacket;

public final class ParticleHelper {

    public static void spawn(Instance instance, Particle particle, Pos loc, int count) {
        spawn(instance, particle, loc, count, 0, 0, 0, 0);
    }

    public static void spawn(Instance instance, Particle particle, Pos loc, int count,
                             double x, double y, double z, double extra) {
        if (instance == null || loc == null) return;
        instance.sendGroupedPacket(new ParticlePacket(particle, loc.x(), loc.y(), loc.z(),
                (float) x, (float) y, (float) z, (float) extra, count));
    }
}
