package kireiko.dev.anticheat.listeners;

import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.events.SVelocityEvent;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.packet.server.play.EntityVelocityPacket;

public final class VelocityListener {

    public static void register(GlobalEventHandler handler) {
        handler.addListener(PlayerPacketOutEvent.class, event -> {
            if (event.getPacket() instanceof EntityVelocityPacket velPacket) {
                handleVelocity(event.getPlayer().getUuid(), velPacket);
            }
        });
    }

    private static void handleVelocity(java.util.UUID uuid, EntityVelocityPacket velPacket) {
        final var player = net.minestom.server.MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(uuid);
        if (player == null) return;
        final PlayerProfile profile = PlayerContainer.getProfile(player);
        if (profile == null) return;

        Vec velocity = velPacket.velocity();
        SVelocityEvent sVelocityEvent = new SVelocityEvent(velocity);
        profile.run(sVelocityEvent);
    }
}
