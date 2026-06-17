package kireiko.dev.anticheat.api.events;

import net.minestom.server.event.Event;
import net.minestom.server.event.player.PlayerPacketEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class CPacketEvent implements Event {
    private PlayerPacketEvent packetEvent;
}
