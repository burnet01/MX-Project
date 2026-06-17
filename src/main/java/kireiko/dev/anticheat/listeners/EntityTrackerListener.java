package kireiko.dev.anticheat.listeners;

import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.utils.cache.EntityCache;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent;

public final class EntityTrackerListener {

    public static void register(GlobalEventHandler handler) {
        handler.addListener(AddEntityToInstanceEvent.class, event -> {
            EntityCache.track(event.getEntity());
        });

        handler.addListener(RemoveEntityFromInstanceEvent.class, event -> {
            EntityCache.untrack(event.getEntity().getEntityId());
        });
    }
}
