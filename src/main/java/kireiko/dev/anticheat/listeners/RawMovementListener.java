package kireiko.dev.anticheat.listeners;

import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.data.RotationsContainer;
import kireiko.dev.anticheat.api.events.MoveEvent;
import kireiko.dev.anticheat.api.events.NoRotationEvent;
import kireiko.dev.anticheat.api.events.RotationEvent;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.api.player.SensitivityProcessor;
import kireiko.dev.anticheat.utils.ConfigCache;
import kireiko.dev.anticheat.utils.protocol.MinestomUtil;
import kireiko.dev.millennium.vectors.Vec2f;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.entity.EntityTeleportEvent;

public final class RawMovementListener {

    public static void register(GlobalEventHandler handler) {
        handler.addListener(EntityTeleportEvent.class, event -> {
            if (!(event.getEntity() instanceof net.minestom.server.entity.Player player)) return;
            final PlayerProfile profile = PlayerContainer.getProfile(player);
            if (profile == null) return;
            profile.setLastTeleport(System.currentTimeMillis());
            profile.setIgnoreFirstTick(true);
        });

        handler.addListener(PlayerMoveEvent.class, event -> {
            final PlayerProfile profile = PlayerContainer.getProfile(event.getPlayer());
            if (profile == null) return;

            Pos from = profile.getTo() != null ? profile.getTo() : event.getPlayer().getPosition();
            Pos to = event.getNewPosition();

            profile.setGround(event.getPlayer().isOnGround());
            profile.setAirTicks(profile.isGround() ? 0 : profile.getAirTicks() + 1);

            profile.setFrom(profile.getTo() != null ? profile.getTo() : from);
            profile.setTo(to);

            boolean hasRotation = from.yaw() != to.yaw() || from.pitch() != to.pitch();

            if (hasRotation) {
                SensitivityProcessor controller = profile.getSensitivityProcessor();
                controller.setLastDeltaPitch(controller.getLastDeltaPitch());
                Vec2f fromRot = new Vec2f((float) from.yaw(), (float) from.pitch());
                Vec2f toRot = new Vec2f((float) to.yaw(), (float) to.pitch());
                RotationEvent rotationEvent = new RotationEvent(profile, toRot, fromRot);
                controller.setDeltaPitch(rotationEvent.getDelta().getY());
                controller.processSensitivity();
                boolean isTeleporting = (System.currentTimeMillis() - profile.getLastTeleport() < 500) || profile.isIgnoreFirstTick();

                if (ConfigCache.ROTATIONS_CONTAINER
                        && !profile.isIgnoreFirstTick()
                        && !isTeleporting) {
                    RotationsContainer.register(MinestomUtil.getUUID(profile.getPlayer()), rotationEvent.getDelta());
                }

                profile.getCinematicComponent().process(rotationEvent);
                if (!isTeleporting) {
                    profile.run(rotationEvent);
                }
                profile.setIgnoreFirstTick(false);
            } else {
                if (!profile.isIgnoreFirstTick() && profile.getLastTeleport() + 1000 < System.currentTimeMillis()) {
                    if (profile.getTo() != null && profile.getFrom() != null) {
                        double dist = profile.getTo().distance(profile.getFrom());
                        if (dist > 1e-4) {
                            profile.run(new NoRotationEvent(profile));
                        }
                    }
                }
            }

            profile.run(new MoveEvent(profile, profile.getTo(), profile.getFrom()));

            if (profile.transactionBoot) LatencyHandler.startChecking(profile);
        });
    }
}
