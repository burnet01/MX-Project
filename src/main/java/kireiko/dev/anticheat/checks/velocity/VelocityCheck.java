package kireiko.dev.anticheat.checks.velocity;

import kireiko.dev.anticheat.api.PacketCheckHandler;
import kireiko.dev.anticheat.api.data.ConfigLabel;
import kireiko.dev.anticheat.api.events.CTransactionEvent;
import kireiko.dev.anticheat.api.events.MoveEvent;
import kireiko.dev.anticheat.api.events.SVelocityEvent;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.managers.CheckManager;
import kireiko.dev.anticheat.services.SimulationFlagService;
import kireiko.dev.anticheat.utils.ConfigCache;
import kireiko.dev.millennium.math.Simplification;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public final class VelocityCheck implements PacketCheckHandler {
    private static final Pattern pattern = Pattern.compile("(?i)(.*(snow|step|frame|table|water|lava|web|slab|stair|ladder|vine|waterlily|wall|carpet|fence|rod|bed|skull|pot|hopper|door|bars|piston|lily).*)");
    private final PlayerProfile profile;
    private final double[] jumpReset = new double[]{0.248136, 0.3332};
    private float vl = 0, totalVlAtY = 0;
    private long oldTime = System.currentTimeMillis();
    private double mostCloseYMotion = 1.0;
    private int timing = 0;
    private boolean transactionLock = false;
    private Vec velocity = null;
    private Pos from = null;
    private boolean isOnGroundFrom = false;
    private Map<String, Object> localCfg = new TreeMap<>();

    @Override
    public ConfigLabel config() {
        localCfg.put("enabled", false);
        localCfg.put("buffer", 6);
        return new ConfigLabel("velocity", localCfg);
    }

    @Override
    public void applyConfig(Map<String, Object> params) {
        localCfg = params;
    }

    @Override
    public Map<String, Object> getConfig() {
        return localCfg;
    }

    public VelocityCheck(PlayerProfile profile) {
        this.profile = profile;
        if (CheckManager.classCheck(this.getClass()))
            this.localCfg = CheckManager.getConfig(this.getClass());
    }

    private static double abs(double v) {
        return Math.abs(v);
    }

    private static double r(double v) {
        return Simplification.scaleVal(v, 6);
    }

    private static boolean ignore(final String block) {
        return pattern.matcher(block).matches();
    }

    @Override
    public void event(Object o) {
        if (!(boolean) localCfg.get("enabled")) return;
        if (o instanceof SVelocityEvent event) {
            this.totalVlAtY = 25;
            this.from = profile.getTo();
            this.applyVelocity(event);
        } else if (o instanceof MoveEvent event) {
            checkVelocity(event);
            this.isOnGroundFrom = profile.isGround();
        } else if (o instanceof CTransactionEvent) {
            transactionLock = false;
        }
    }

    private void applyVelocity(SVelocityEvent event) {
        transactionLock = true;
        Pos[] locationsToCheck = {
                this.profile.getTo().add(event.getVelocity()),
                this.profile.getTo().add(event.getVelocity()).add(0, 1, 0)
        };
        boolean allClear = true;
        for (Pos loc : locationsToCheck) {
            if (isPointWall(loc, 0.3)) {
                allClear = false;
                break;
            }
        }
        if (allClear) {
            this.velocity = event.getVelocity();
            this.mostCloseYMotion = 1.0;
            this.timing = 0;
        }
        if (vl > 0) vl -= 5;
    }

    private void checkVelocity(MoveEvent event) {
        final long delay = System.currentTimeMillis() - oldTime;
        Pos from = event.getFrom();
        Pos to = event.getTo();

        if (isPointWall(to.add(0, 1, 0), 0.75)) {
            velocity = null;
        }

        final double x = -(to.x() - from.x());
        final double y = -(to.y() - from.y());
        final double z = -(to.z() - from.z());

        if (velocity != null) {
            if (abs(abs(y) - abs(velocity.y())) < mostCloseYMotion)
                mostCloseYMotion = y;
            if (abs(abs(y) - abs(velocity.y())) < 0.005) {
                { // time for horizontal
                    double xDiff = x - velocity.x();
                    double zDiff = z - velocity.z();
                    double total = abs(xDiff) + abs(zDiff);
                    double multi = 1.0;
                    if ((!this.profile.isGround() && isOnGroundFrom)) {
                        if (total > 0.2 * multi) {
                            this.flag("Velocity", "Horizontal", "[Air] " + total, 0.0f, 14);
                        }
                    } else if (total > 0.21 * multi) {
                        this.flag("Velocity", "Horizontal", "[Ground] " + total, 0.0f, 30);
                    }
                }
                this.velocity = null;
                this.timing = 0;
            } else if (delay > 25) {
                if (isJumpReset(y)) totalVlAtY = 12;
                if (timing <= 3) {
                    if (!transactionLock) timing++;
                } else {
                    if (this.velocity.y() != 0.003) {
                        this.flag("Velocity", "Vertical", "diff=" + r(Math.abs(velocity.y() - mostCloseYMotion))
                                + ((totalVlAtY > 12) ? " [Basic]" : " [JumpReset]"), 0.0f, totalVlAtY);
                    }
                    this.velocity = null;
                }
            }
        }
        this.oldTime = System.currentTimeMillis();
    }

    private boolean isPointWall(Pos location, final double scale) {
        final double x = location.x();
        final double y = location.y() + 0.1;
        final double z = location.z();
        Instance instance = profile.getPlayer().getInstance();
        if (instance == null) return false;

        for (int dx = -1; dx <= 1; ++dx) {
            for (int dy = -1; dy <= 1; ++dy) {
                for (int dz = -1; dz <= 1; ++dz) {
                    final Block block = instance.getBlock(
                            (int) (x + (double) dx * scale),
                            (int) (y + (double) dy * scale),
                            (int) (z + (double) dz * scale)
                    );

                    if (!block.compare(Block.AIR) || ignore(block.name().toLowerCase()) || block.name().contains("GRASS")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void flag(final String check, final String component, final String info, final float m, float vl) {
        this.vl += vl;
        float vlLimit = ((Number) localCfg.get("buffer")).floatValue() * 10f;
        if (this.vl > vlLimit) {
            this.profile.punish(check, component, info, m);
            SimulationFlagService.getFlags().add(new SimulationFlagService.Flag(profile, from, velocity));
            this.vl = vlLimit - 10;
        }
    }

    private boolean isJumpReset(double v) {
        for (double d : this.jumpReset) {
            if (Math.abs(d - v) < 0.005) return true;
        }
        return false;
    }
}
