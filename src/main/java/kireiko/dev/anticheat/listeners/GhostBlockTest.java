package kireiko.dev.anticheat.listeners;

import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;

public final class GhostBlockTest {

    public static void register(GlobalEventHandler handler) {
        handler.addListener(PlayerBlockPlaceEvent.class, event -> {
            event.setCancelled(true);
        });

        handler.addListener(PlayerBlockInteractEvent.class, event -> {
            event.setCancelled(true);
        });
    }
}
