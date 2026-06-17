package kireiko.dev.anticheat.api.player.fun;

import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.utils.enums.ParticleTypes;
import kireiko.dev.anticheat.utils.helper.ParticleHelper;
import kireiko.dev.millennium.math.*;
import kireiko.dev.millennium.vectors.Vec2;
import kireiko.dev.millennium.vectors.Vec3;
import lombok.Data;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.particle.Particle;
import net.minestom.server.entity.damage.Damage;

@Data
public final class Rocket implements FunThing {
    private final PlayerProfile linked;
    private final PlayerProfile target;
    private final Pos location;
    private double speed;
    private boolean destroyed;
    private boolean optimizer3000;

    public Rocket(final PlayerProfile linked, final PlayerProfile target, final Pos location) {
        this.linked = linked;
        this.target = target;
        this.location = location;
        this.destroyed = false;
        this.speed = 0.25;
        this.optimizer3000 = false;
    }

    @Override
    public void tick() {
        this.optimizer3000 = !optimizer3000;
        final double speed = getSpeed();
        if (this.speed < 0.85) this.speed += 0.005;
        final Vec2 vec2 = Euler.calculateVec2Vec(new Vec3(location), new Vec3(target.getTo()));
        final float yaw = (float) vec2.getX();
        final float pitch = (float) vec2.getY();
        final Vec direction = new Vec(
                -GeneralMath.sin((float) Math.toRadians(yaw), BuildSpeed.FAST),
                -GeneralMath.sin((float) Math.toRadians(pitch), BuildSpeed.FAST),
                GeneralMath.cos((float) Math.toRadians(yaw), BuildSpeed.FAST));
        final double interpolatePitch = 1 - ((Math.abs(pitch) * 1.1111) / 100);
        double newX = direction.x() * interpolatePitch * speed;
        double newZ = direction.z() * interpolatePitch * speed;

        Pos newLoc = location.add(newX, 0, newZ);

        { // bound
            final double hitbox = 0.5;
            double x = target.getTo().x(),
                    y = target.getTo().y(),
                    z = target.getTo().z();
            if (RayTrace.doRayTrace(BuildSpeed.FAST,
                    new Vec2(vec2.getY(), vec2.getX()), new Vec3(location),
                    new AxisAlignedBB(
                            x - hitbox, y - 0.1f, z - hitbox,
                            x + hitbox, y + 1.9f, z + hitbox
                    ), 0.85)) {
                { // boom!
                    this.destroyed = true;
                    MinecraftServer.getSchedulerManager().buildTask(() -> {
                        target.getPlayer().kill();
                    }).schedule();
                    ParticleHelper.spawn(linked.getPlayer().getInstance(), Particle.EXPLOSION, location, 1);
                }
            }
        }
        if (optimizer3000) { // animation
            ParticleHelper.spawn(linked.getPlayer().getInstance(), Particle.FLAME, location, 1);
            ParticleHelper.spawn(linked.getPlayer().getInstance(), Particle.SMOKE, location, 1);
        }
    }
}
