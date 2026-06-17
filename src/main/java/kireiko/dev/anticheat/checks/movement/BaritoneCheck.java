package kireiko.dev.anticheat.checks.movement;

import kireiko.dev.anticheat.api.PacketCheckHandler;
import kireiko.dev.anticheat.api.data.ConfigLabel;
import kireiko.dev.anticheat.api.events.MoveEvent;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.managers.CheckManager;

import java.util.Map;
import java.util.TreeMap;

public final class BaritoneCheck implements PacketCheckHandler {
    private final PlayerProfile profile;
    private Map<String, Object> localCfg = new TreeMap<>();

    @Override
    public ConfigLabel config() {
        localCfg.put("enabled", false);
        return new ConfigLabel("baritone", localCfg);
    }

    @Override
    public void applyConfig(Map<String, Object> params) {
        localCfg = params;
    }

    @Override
    public Map<String, Object> getConfig() {
        return localCfg;
    }

    public BaritoneCheck(PlayerProfile profile) {
        this.profile = profile;
        if (CheckManager.classCheck(this.getClass()))
            this.localCfg = CheckManager.getConfig(this.getClass());
    }

    @Override
    public void event(Object o) {
        if (!(boolean) localCfg.get("enabled")) return;
        if (o instanceof MoveEvent event) {
            // Simplified baritone check
            // This would need more sophisticated path analysis
        }
    }
}
