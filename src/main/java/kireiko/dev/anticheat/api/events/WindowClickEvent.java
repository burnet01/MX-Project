package kireiko.dev.anticheat.api.events;

import net.minestom.server.event.Event;
import net.minestom.server.event.player.PlayerPacketEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class WindowClickEvent implements Event {
    private PlayerPacketEvent packetEvent;
}
