package kireiko.dev.anticheat.listeners;

import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.events.CPacketEvent;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerPacketEvent;

public final class OmniPacketListener {

    public static void register(GlobalEventHandler handler) {
        handler.addListener(PlayerPacketEvent.class, event -> {

            PlayerProfile protocol = PlayerContainer.getProfile(event.getPlayer());
            if (protocol == null) return;
            protocol.run(new CPacketEvent(event));
        });
    }
}
