package kireiko.dev.anticheat.api.events;

import kireiko.dev.anticheat.api.player.PlayerProfile;
import net.minestom.server.entity.Player;
import net.kyori.adventure.bossbar.BossBar;
import net.minestom.server.event.trait.CancellableEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
public final class MXFlagEvent implements CancellableEvent {

    private final Player player;
    private final String check;
    private final String component;
    private final String info;
    private final float vl;
    private final double vlLimit;
    @Setter
    private boolean cancelled;

    public MXFlagEvent(Player player, String check, String component, String info, float vl, double vlLimit) {
        this.player = player;
        this.check = check;
        this.component = component;
        this.info = info;
        this.vl = vl;
        this.vlLimit = vlLimit;
    }
}
