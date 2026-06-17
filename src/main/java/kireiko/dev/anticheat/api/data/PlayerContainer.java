package kireiko.dev.anticheat.api.data;

import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.core.AsyncScheduler;
import kireiko.dev.anticheat.utils.LogUtils;
import lombok.Getter;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerContainer {

    @Getter
    private static final Map<UUID, PlayerProfile> uuidPlayerProfileMap = new ConcurrentHashMap<>();

    public static void init(Player player) {
        AsyncScheduler.run(() -> {
            PlayerProfile profile = new PlayerProfile(player);
            uuidPlayerProfileMap.put(player.getUuid(), profile);
            profile.initChecks(profile);
        });
    }

    public static void unload(Player player) {
        PlayerProfile profile = uuidPlayerProfileMap.get(player.getUuid());
        if (profile == null) return;
        if (!profile.getLogs().isEmpty()) {
            final StringBuilder logBuilder = new StringBuilder();
            LogUtils.createLog(player.getUsername());
            for (final String l : profile.getLogs()) logBuilder.append("\n").append(l);
            LogUtils.addLog(player.getUsername(), logBuilder.toString());
            profile.getLogs().clear();
        }
        uuidPlayerProfileMap.remove(player.getUuid());
        if (profile.getBanAnimInfo() != null && !profile.isIgnoreExitBan()) {
            profile.forcePunish(profile.getBanAnimInfo().getX(), profile.getBanAnimInfo().getY());
        }
    }

    @Nullable
    public static PlayerProfile getProfile(Player player) {
        return uuidPlayerProfileMap.get(player.getUuid());
    }

    @Nullable
    public static PlayerProfile getProfileByName(String name) {
        for (PlayerProfile profile : uuidPlayerProfileMap.values()) {
            if (profile.getPlayer().getUsername().equalsIgnoreCase(name)) {
                return profile;
            }
        }
        return null;
    }
}
