package kireiko.dev.anticheat.api.events;

import kireiko.dev.anticheat.api.player.PlayerProfile;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class MoveEvent {
    private PlayerProfile profile;
    private Pos from;
    private Pos to;

    public Vec getDelta() {
        return new Vec(
                to.x() - from.x(),
                to.y() - from.y(),
                to.z() - from.z()
        );
    }
}
