package kireiko.dev.anticheat.core;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;

import java.util.concurrent.CompletableFuture;

public final class AsyncEntityFetcher {
    public static CompletableFuture<Entity> getEntityFromIDAsync(Instance world, int entityId) {
        CompletableFuture<Entity> future = new CompletableFuture<>();
        try {
            Entity entity = world.getEntityById(entityId);
            future.complete(entity);
        } catch (Exception ex) {
            future.completeExceptionally(ex);
        }
        return future;
    }
}
