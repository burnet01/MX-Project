package kireiko.dev.anticheat.api.player.fun;

import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.utils.enums.ParticleTypes;
import kireiko.dev.anticheat.utils.helper.ParticleHelper;
import kireiko.dev.millennium.math.AxisAlignedBB;
import kireiko.dev.millennium.math.BuildSpeed;
import kireiko.dev.millennium.math.GeneralMath;
import kireiko.dev.millennium.math.RayTrace;
import kireiko.dev.millennium.vectors.Vec2;
import kireiko.dev.millennium.vectors.Vec3;
import lombok.Data;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.particle.Particle;
import net.minestom.server.entity.damage.Damage;

import static kireiko.dev.anticheat.utils.protocol.ProtocolTools.getBlockAsync;

@Data
public final class Spell implements FunThing {
    private final PlayerProfile linked;
    private final Pos location;
    private final ParticleTypes effect, explosion;
    private double speed, damage;
    private boolean destroyed;
    private boolean optimizer3000;
    private int exist;

    public Spell(final PlayerProfile linked, final Pos location,
                 final double speed, final double damage, final ParticleTypes effect,
                 final ParticleTypes explosion, Object potionEffect) {
        this.linked = linked;
        this.location = location;
        this.destroyed = false;
        this.speed = speed;
        this.damage = damage;
        this.effect = effect;
        this.explosion = explosion;
        this.optimizer3000 = false;
        this.exist = 0;
    }

    @Override
    public void tick() {
        this.optimizer3000 = !optimizer3000;
        final double speed = getSpeed();
        final float yaw = (float) location.yaw();
        final float pitch = (float) location.pitch();
        final Vec direction = new Vec(
                -GeneralMath.sin((float) Math.toRadians(yaw), BuildSpeed.FAST),
                -GeneralMath.sin((float) Math.toRadians(pitch), BuildSpeed.FAST),
                GeneralMath.cos((float) Math.toRadians(yaw), BuildSpeed.FAST));
        final double interpolatePitch = 1 - ((Math.abs(pitch) * 1.1111) / 100);
        double newX = direction.x() * interpolatePitch * speed;
        double newZ = direction.z() * interpolatePitch * speed;

        Pos newLoc = location.add(newX, 0, newZ);
        this.exist++;
        { // bound player
            final double hitbox = 0.4;
            for (PlayerProfile target : PlayerContainer.getUuidPlayerProfileMap().values()) {
                if (target.getPlayer().getUuid().equals(linked.getPlayer().getUuid())) continue;
                Instance instance = linked.getPlayer().getInstance();
                if (instance != null && linked.getPlayer().getInstance() == instance
                        && location.distance(target.getPlayer().getPosition()) < 5) {
                    double x = target.getTo().x(),
                            y = target.getTo().y(),
                            z = target.getTo().z();
                    if (RayTrace.doRayTrace(BuildSpeed.FAST,
                            new Vec2(linked.getTo().pitch(), linked.getTo().yaw()), new Vec3(location),
                            new AxisAlignedBB(
                                    x - hitbox, y - 0.1f, z - hitbox,
                                    x + hitbox, y + 1.9f, z + hitbox
                            ), speed + 0.4)) {
                        { // boom!
                            this.destroyed = true;
                            MinecraftServer.getSchedulerManager().buildTask(() -> {
                                Pos attackerLoc = linked.getPlayer().getPosition();
                                Vec attackDirection = linked.getPlayer().getPosition().direction();

                                Vec horizontalKnockback = new Vec(
                                        -attackDirection.x(),
                                        0,
                                        -attackDirection.z()
                                ).normalize();

                                double vertical = 0.35 * (1 + (exist / 1200.0));
                                double horizontal = -0.45;

                                Vec velocity = horizontalKnockback
                                        .mul(horizontal)
                                        .withY(vertical);

                                target.getPlayer().setVelocity(velocity);
                                target.getPlayer().damage(Damage.fromEntity(linked.getPlayer(), (float) damage));
                            }).schedule();
                            ParticleHelper.spawn(linked.getPlayer().getInstance(), Particle.EXPLOSION, location, 1);
                            break;
                        }
                    }
                }
            }
        }
        { // bound block
            double x = location.x();
            double y = location.y();
            double z = location.z();

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
                                destroyed = true;
                                ParticleHelper.spawn(linked.getPlayer().getInstance(), Particle.EXPLOSION, location, 5);
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (optimizer3000) { // animation
            ParticleHelper.spawn(linked.getPlayer().getInstance(), Particle.FLAME, location, 1);
        }
        if (exist > 1200) destroyed = true;
    }
}
