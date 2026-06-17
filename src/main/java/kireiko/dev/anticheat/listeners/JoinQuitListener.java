package kireiko.dev.anticheat.listeners;

import kireiko.dev.anticheat.api.data.PlayerContainer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;

public final class JoinQuitListener {

    public static void register(GlobalEventHandler handler) {
        handler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            PlayerContainer.init(event.getPlayer());
        });

        handler.addListener(PlayerDisconnectEvent.class, event -> {
            PlayerContainer.unload(event.getPlayer());
        });
    }
}
