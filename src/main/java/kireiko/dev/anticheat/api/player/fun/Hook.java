package kireiko.dev.anticheat.api.player.fun;

import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.utils.enums.ParticleTypes;
import kireiko.dev.anticheat.utils.helper.ParticleHelper;
import kireiko.dev.millennium.math.BuildSpeed;
import kireiko.dev.millennium.math.Euler;
import kireiko.dev.millennium.math.GeneralMath;
import kireiko.dev.millennium.math.Interpolation;
import kireiko.dev.millennium.vectors.Vec2;
import kireiko.dev.millennium.vectors.Vec3;
import lombok.Data;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.particle.Particle;

import static kireiko.dev.anticheat.utils.protocol.ProtocolTools.getBlockAsync;

@Data
public class Hook implements FunThing {
    private final PlayerProfile linked;
    private final Pos location;
    private boolean stuck;
    private double yPhys;
    private int hoverTicks;
    private boolean optimizer3000;

    public Hook(final PlayerProfile linked, final Pos location) {
        this.linked = linked;
        this.location = location;
        this.stuck = false;
        this.hoverTicks = 0;
        this.yPhys = 0;
        this.optimizer3000 = false;
    }

    @Override
    public void tick() {
        this.optimizer3000 = !optimizer3000;
        if (!stuck) {
            final double speed = 1.1;
            final float yaw = (float) location.yaw();
            final float pitch = (float) location.pitch();
            final Vec direction = new Vec(
                    -GeneralMath.sin((float) Math.toRadians(yaw), BuildSpeed.FAST),
                    -GeneralMath.sin((float) Math.toRadians(pitch), BuildSpeed.FAST),
                    GeneralMath.cos((float) Math.toRadians(yaw), BuildSpeed.FAST));
            final double interpolatePitch = 1 - ((Math.abs(pitch) * 1.1111) / 100);
            double newX = direction.x() * interpolatePitch * speed;
            double newZ = direction.z() * interpolatePitch * speed;

            Pos newLoc = location.add(newX, yPhys, newZ);
            yPhys -= 0.98e-2;

            { // bound
                double x = newLoc.x();
                double y = newLoc.y();
                double z = newLoc.z();

                Instance instance = linked.getPlayer().getInstance();
                if (instance != null) {
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                Block block = getBlockAsync(instance,
                                        (int) (x + (dx * 0.3)),
                                        (int) (y + (dy * 0.3)),
                                        (int) (z + (dz * 0.3))
                                );
                                if (block == null) continue;
                                if (!block.compare(Block.AIR)) {
                                    stuck = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (hoverTicks < 20) {
                hoverTicks++;
                Instance instance = linked.getPlayer().getInstance();
                if (instance != null) {
                    final Vec2 vec2 = Euler.calculateVec2Vec(new Vec3(linked.getTo()), new Vec3(location));
                    final double speed = linked.getPlayer().getPosition().distance(location) / 14d;
                    final double x = -Math.sin(Math.toRadians(vec2.getX())) * speed;
                    final double y = (location.y() - linked.getTo().y())
                            / Interpolation.sineInterpolation(30d, 6d, hoverTicks / 19d, Interpolation.Ease.IN);
                    final double z = Math.cos(Math.toRadians(vec2.getX())) * speed;
                    linked.getPlayer().setVelocity(new Vec(x, y, z));
                }
            }
        }
        if (hoverTicks < 20 && optimizer3000) { // animation
            for (double d = 0; d < 1.0; d += 0.05) {
                final Pos to = linked.getTo();
                final Pos i = new Pos(
                        Interpolation.sineInterpolation(to.x(), location.x(), d, Interpolation.Ease.IN),
                        Interpolation.sineInterpolation(to.y(), location.y(), d, Interpolation.Ease.IN),
                        Interpolation.sineInterpolation(to.z(), location.z(), d, Interpolation.Ease.IN)
                );
                ParticleHelper.spawn(linked.getPlayer().getInstance(), Particle.CRIT, i, 1);
            }
        }
    }
}
