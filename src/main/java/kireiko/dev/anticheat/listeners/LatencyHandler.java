package kireiko.dev.anticheat.listeners;

import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.events.CTransactionEvent;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.common.ClientKeepAlivePacket;

public final class LatencyHandler {

    public static void register(GlobalEventHandler handler) {
        handler.addListener(PlayerPacketEvent.class, event -> {
            if (event.getPacket() instanceof ClientKeepAlivePacket) {
                handlePong(event);
            }
        });
    }

    private static void handlePong(PlayerPacketEvent event) {
        final var player = event.getPlayer();
        final PlayerProfile protocol = PlayerContainer.getProfile(player);
        if (protocol == null) return;

        if (protocol.transactionBoot) return;

        protocol.transactionPing = player.getLatency();
        protocol.getPing().add(protocol.transactionPing);
        protocol.transactionLastTime = System.currentTimeMillis();
        protocol.transactionSentKeep = false;
        protocol.run(new CTransactionEvent(protocol));
    }

    public static void startChecking(PlayerProfile protocol) {
        protocol.transactionBoot = false;
    }
}
