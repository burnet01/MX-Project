package kireiko.dev.anticheat.listeners;

import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.events.EntityActionEvent;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerStartSneakingEvent;
import net.minestom.server.event.player.PlayerStartSprintingEvent;
import net.minestom.server.event.player.PlayerStopSneakingEvent;
import net.minestom.server.event.player.PlayerStopSprintingEvent;

public final class EntityActionListener {

    public static void register(GlobalEventHandler handler) {
        handler.addListener(PlayerStartSprintingEvent.class, event -> {
            PlayerProfile protocol = PlayerContainer.getProfile(event.getPlayer());
            if (protocol == null) return;
            protocol.sprinting = true;
            protocol.run(new EntityActionEvent(AbilitiesEnum.START_SPRINTING));
        });

        handler.addListener(PlayerStopSprintingEvent.class, event -> {
            PlayerProfile protocol = PlayerContainer.getProfile(event.getPlayer());
            if (protocol == null) return;
            protocol.sprinting = false;
            protocol.run(new EntityActionEvent(AbilitiesEnum.STOP_SPRINTING));
        });

        handler.addListener(PlayerStartSneakingEvent.class, event -> {
            PlayerProfile protocol = PlayerContainer.getProfile(event.getPlayer());
            if (protocol == null) return;
            protocol.sneaking = true;
            protocol.run(new EntityActionEvent(AbilitiesEnum.PRESS_SHIFT_KEY));
        });

        handler.addListener(PlayerStopSneakingEvent.class, event -> {
            PlayerProfile protocol = PlayerContainer.getProfile(event.getPlayer());
            if (protocol == null) return;
            protocol.sneaking = false;
            protocol.run(new EntityActionEvent(AbilitiesEnum.RELEASE_SHIFT_KEY));
        });
    }

    public enum AbilitiesEnum {
        START_SPRINTING,
        STOP_SPRINTING,
        PRESS_SHIFT_KEY,
        RELEASE_SHIFT_KEY
    }
}
