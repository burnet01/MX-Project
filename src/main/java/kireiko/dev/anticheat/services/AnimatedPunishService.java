package kireiko.dev.anticheat.services;

import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.core.AsyncScheduler;
import kireiko.dev.millennium.vectors.Pair;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.particle.Particle;
import net.minestom.server.network.packet.server.play.ParticlePacket;

import net.minestom.server.utils.time.TimeUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AnimatedPunishService {
    private static final List<PlayerProfile> punished = new ArrayList<>();
    private static List<Object[]> endAnim = new ArrayList<>();

    public static void punish(PlayerProfile profile, Pair<String, String> bane) {
        profile.setBanAnimPositions(new Pair<>(profile.getTo(), profile.getTo()));
        profile.setBanAnimInfo(bane);
        punished.add(profile);
    }

    public static void init() {
        MinecraftServer.getSchedulerManager().buildTask(() -> {
            AsyncScheduler.run(() -> {
                punishAnim();
                outAnim();
            });
        }).repeat(1, TimeUnit.SERVER_TICK).schedule();
    }

    private static void punishAnim() {
        Set<PlayerProfile> rm = new HashSet<>();
        for (PlayerProfile profile : punished) {
            if (profile.punishAnimation > 100) {
                MinecraftServer.getSchedulerManager().buildTask(() -> {
                    for (int i = 0; i < 10; i++) {
                        spawnParticle(profile.getPlayer().getInstance(), profile.getTo(), Particle.FLAME, 10);
                    }
                    endAnim.add(new Object[]{profile.getTo(), 0, profile.getPlayer().getInstance()});
                    profile.getPlayer().teleport(profile.getBanAnimPositions().getY());
                    MinecraftServer.getSchedulerManager().buildTask(() -> {
                        if (profile.getBanAnimInfo() != null) {
                            profile.getPlayer().kill();
                            profile.forcePunish(profile.getBanAnimInfo().getX(), profile.getBanAnimInfo().getY());
                        }
                    }).delay(1, TimeUnit.SERVER_TICK).schedule();
                }).schedule();
                rm.add(profile);
            } else {
                playAnimation(profile, profile.punishAnimation);
            }
            profile.punishAnimation += 2;
        }
        for (PlayerProfile profile : rm)
            punished.remove(profile);
        rm.clear();
    }

    private static void outAnim() {
        List<Object[]> endAnimCopy = new ArrayList<>(endAnim);
        for (Object[] object : endAnimCopy) {
            Pos l = (Pos) object[0];
            int progress = (int) object[1];
            double d = circIn(0, 20, progress);
            if (progress > 100) {
                endAnim.remove(object);
            } else {
                for (int i = 0; i < 360; i += 30) {
                    double angle = Math.toRadians(i);
                    for (double y = -7; y < 4; y++) {
                        Pos particlePos = new Pos(l.x() + -Math.sin(angle) * d, l.y() + y, l.z() + Math.cos(angle) * d);
                        spawnParticleAtPos(particlePos, Particle.CRIT, 1);
                    }
                }
                object[1] = ((int) object[1]) + 4;
            }
        }
        endAnim = endAnimCopy;
    }

    private static void playAnimation(PlayerProfile iPlayer, int percent) {
        Pos l = iPlayer.getBanAnimPositions().getY().add(0, circOut(0, 5, percent), 0);

        { // chest anim
            Pos l1 = l.add(0, -0.4, 0);
            double d = 2.5 - circOut(0, 2, percent);
            double circular = 2;
            double angle1 = Math.toRadians(percent * (3.6 * circular));
            double angle2 = Math.toRadians((percent * (3.6 * circular)) + 180);
            spawnParticleAtPos(new Pos(l1.x() + -Math.sin(angle1) * d, l1.y(), l1.z() + Math.cos(angle1) * d), Particle.DRIPPING_LAVA, 1);
            spawnParticleAtPos(new Pos(l1.x() + -Math.sin(angle2) * d, l1.y(), l1.z() + Math.cos(angle2) * d), Particle.DRIPPING_LAVA, 1);
        }

        { // coming explosion anim
            Pos l1 = l.add(0, 1, 0);
            double d = 12 - circOut(0, 11.9, percent);
            double circular = 1;
            double angle1 = Math.toRadians(percent * (3.6 * circular));
            double angle2 = Math.toRadians((percent * (3.6 * circular)) + 180);
            spawnParticleAtPos(new Pos(l1.x() + -Math.sin(angle1) * d, l1.y(), l1.z() + Math.cos(angle1) * d), Particle.ANGRY_VILLAGER, 2);
            spawnParticleAtPos(new Pos(l1.x() + -Math.sin(angle2) * d, l1.y(), l1.z() + Math.cos(angle2) * d), Particle.ANGRY_VILLAGER, 2);
        }

        { // mystic particles
            for (double d = 0; d < 5; d += 0.4) {
                Pos l1 = l.add(0, d, 0);
                spawnParticleAtPos(l1, Particle.ENCHANT, 1);
            }
        }

        MinecraftServer.getSchedulerManager().buildTask(() ->
                iPlayer.getPlayer().teleport(l)
        ).schedule();
    }

    private static void spawnParticle(net.minestom.server.instance.Instance instance, Pos location, Particle particle, int count) {
        if (instance == null || location == null) return;
        ParticlePacket packet = new ParticlePacket(particle, location, null, 0, count);
        for (Player player : instance.getPlayers()) {
            player.sendPacket(packet);
        }
    }

    private static void spawnParticleAtPos(Pos location, Particle particle, int count) {
        if (location == null) return;
        net.minestom.server.instance.Instance instance = MinecraftServer.getInstanceManager().getInstances().stream().findFirst().orElse(null);
        if (instance != null) {
            ParticlePacket packet = new ParticlePacket(particle, location, null, 0, count);
            for (Player player : instance.getPlayers()) {
                player.sendPacket(packet);
            }
        }
    }

    private static double circOut(double from, double to, int percent) {
        percent = Math.max(0, Math.min(100, percent));
        double change = to - from;
        double progress = percent / 100.0;
        return from + change * Math.sqrt(1 - Math.pow(progress - 1, 2));
    }

    public static double circIn(double from, double to, int percent) {
        percent = Math.max(0, Math.min(100, percent));
        double change = to - from;
        double progress = percent / 100.0;
        return from - change * (Math.sqrt(1 - Math.pow(progress, 2)) - 1);
    }
}
