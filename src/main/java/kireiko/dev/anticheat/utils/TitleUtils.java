package kireiko.dev.anticheat.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.minestom.server.entity.Player;

import java.time.Duration;

public final class TitleUtils {

    public static void sendTitle(Player player, String title, String subtitle,
                                 int fadeIn, int stay, int fadeOut) {
        Component titleComponent = MessageUtils.wrapColorsToComponent(title);
        Component subtitleComponent = MessageUtils.wrapColorsToComponent(subtitle);

        Title.Times times = Title.Times.times(
                Duration.ofMillis(fadeIn * 50L),
                Duration.ofMillis(stay * 50L),
                Duration.ofMillis(fadeOut * 50L)
        );

        player.showTitle(Title.title(titleComponent, subtitleComponent, times));
    }
}
