package kireiko.dev.anticheat.listeners;

import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.events.UseEntityEvent;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.utils.ConfigCache;
import kireiko.dev.anticheat.utils.cache.EntityCache;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientInteractEntityPacket;

public final class UseEntityListener {

    public static void register(GlobalEventHandler handler) {
        handler.addListener(PlayerPacketEvent.class, event -> {
            if (!(event.getPacket() instanceof ClientInteractEntityPacket interactPacket)) return;

            var player = event.getPlayer();
            PlayerProfile profile = PlayerContainer.getProfile(player);
            if (profile == null) return;

            boolean attack = interactPacket.location() == null;
            int entityId = interactPacket.targetId();
            Entity entity = EntityCache.get(entityId);

            if (profile.getAttackBlockToTime() > System.currentTimeMillis()) {
                if (ConfigCache.PREVENTION > 0) {
                    if (ConfigCache.PREVENTION >= 3) {
                        player.teleport(player.getPosition());
                    } else if (ConfigCache.PREVENTION == 1
                            && attack
                            && entity instanceof LivingEntity target) {
                        double distance = player.getPosition().distance(entity.getPosition());
                        if (distance < 3.3) {
                            target.damage(Damage.fromEntity(player, 0.5f));
                        }
                    }
                    profile.debug("UseEntity packet blocked");
                    MX.blockedPerMinuteCount++;
                }
            }

            UseEntityEvent e = new UseEntityEvent(entity, attack, entityId, false);
            profile.run(e);
        });
    }
}
