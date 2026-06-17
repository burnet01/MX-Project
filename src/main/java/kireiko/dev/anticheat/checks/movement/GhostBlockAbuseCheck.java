package kireiko.dev.anticheat.checks.movement;

import kireiko.dev.anticheat.api.PacketCheckHandler;
import kireiko.dev.anticheat.api.data.ConfigLabel;
import kireiko.dev.anticheat.api.events.MoveEvent;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.managers.CheckManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.Map;
import java.util.TreeMap;

public final class GhostBlockAbuseCheck implements PacketCheckHandler {
    private final PlayerProfile profile;
    private Map<String, Object> localCfg = new TreeMap<>();

    @Override
    public ConfigLabel config() {
        localCfg.put("enabled", false);
        return new ConfigLabel("ghost_block_abuse", localCfg);
    }

    @Override
    public void applyConfig(Map<String, Object> params) {
        localCfg = params;
    }

    @Override
    public Map<String, Object> getConfig() {
        return localCfg;
    }

    public GhostBlockAbuseCheck(PlayerProfile profile) {
        this.profile = profile;
        if (CheckManager.classCheck(this.getClass()))
            this.localCfg = CheckManager.getConfig(this.getClass());
    }

    @Override
    public void event(Object o) {
        if (!(boolean) localCfg.get("enabled")) return;
        if (o instanceof MoveEvent event) {
            // Simplified ghost block check
            Pos to = event.getTo();
            if (to != null) {
                Instance instance = profile.getPlayer().getInstance();
                if (instance != null) {
                    Block block = instance.getBlock((int) to.x(), (int) to.y(), (int) to.z());
                    if (block.compare(Block.AIR)) {
                        // Player is in air block, might be ghost blocking
                        // This is a simplified check
                    }
                }
            }
        }
    }
}
