package kireiko.dev.anticheat.utils;

import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MessageUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("(?i)&#([A-F0-9]{6})");
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacySection();

    public static void sendMessagesToPlayers(String permission, String message) {
        for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            PlayerProfile profile = PlayerContainer.getProfile(player);
            if (profile == null || !profile.isAlerts()) {
                continue;
            }
            if (player.getPermissionLevel() >= 1) {
                player.sendMessage(wrapColorsToComponent(message));
            }
        }
    }

    public static void sendMessagesToPlayersNative(String permission, String permission2, String message) {
        for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            if (player.getPermissionLevel() >= 1) {
                player.sendMessage(wrapColorsToComponent(message));
            }
        }
    }

    public static String wrapColors(String input) {
        if (input == null) return null;
        Matcher matcher = HEX_PATTERN.matcher(input);
        StringBuffer sb = new StringBuffer(input.length() * 2);

        while (matcher.find()) {
            String hexCode = "#" + matcher.group(1);
            String replacement = "§x§" + hexCode.charAt(1) + "§" + hexCode.charAt(2) + "§" + hexCode.charAt(3) + "§" + hexCode.charAt(4) + "§" + hexCode.charAt(5) + "§" + hexCode.charAt(6);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    public static Component wrapColorsToComponent(String input) {
        return SERIALIZER.deserialize(wrapColors(input));
    }

    public static String wrapColors(String... v) {
        final StringBuilder builder = new StringBuilder();
        for (final String s : v) {
            final String wrapped = wrapColors(s);
            builder.append((builder.length() == 0) ? wrapped : "\n" + wrapped);
        }
        return builder.toString();
    }

    public static String getDate() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd:MM:yyyy");
        return sdf.format(date);
    }
}
