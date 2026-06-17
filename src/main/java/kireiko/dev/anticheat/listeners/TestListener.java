package kireiko.dev.anticheat.listeners;

import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerPacketEvent;

public final class TestListener {

    public static void register(GlobalEventHandler handler) {
        handler.addListener(PlayerPacketEvent.class, event -> {
            event.getPlayer().sendMessage("e: " + event.getPacket().getClass().getSimpleName());
        });
    }
}
