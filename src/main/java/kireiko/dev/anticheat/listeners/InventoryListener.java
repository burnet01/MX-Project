package kireiko.dev.anticheat.listeners;

import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.events.WindowClickEvent;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientClickWindowPacket;

public final class InventoryListener {

    public static void register(GlobalEventHandler handler) {
        handler.addListener(PlayerPacketEvent.class, event -> {
            if (!(event.getPacket() instanceof ClientClickWindowPacket)) return;

            PlayerProfile protocol = PlayerContainer.getProfile(event.getPlayer());
            if (protocol == null) return;
            protocol.run(new WindowClickEvent(event));
        });
    }
}
