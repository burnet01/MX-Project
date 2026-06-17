package kireiko.dev.anticheat.listeners;

import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientVehicleMovePacket;

public final class VehicleTeleportListener {

    public static void register(GlobalEventHandler handler) {
        handler.addListener(PlayerPacketEvent.class, event -> {
            if (!(event.getPacket() instanceof ClientVehicleMovePacket)) return;

            PlayerProfile protocol = PlayerContainer.getProfile(event.getPlayer());
            if (protocol == null) return;
            protocol.setLastTeleport(System.currentTimeMillis());
            protocol.setIgnoreFirstTick(true);
        });
    }
}
