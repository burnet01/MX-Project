package kireiko.dev.anticheat;

import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.commands.MXCommandHandler;
import kireiko.dev.anticheat.core.AsyncScheduler;
import kireiko.dev.anticheat.listeners.*;
import kireiko.dev.anticheat.managers.CheckManager;
import kireiko.dev.anticheat.services.AnimatedPunishService;
import kireiko.dev.anticheat.services.FunThingsService;
import kireiko.dev.anticheat.services.SimulationFlagService;
import kireiko.dev.anticheat.utils.ConfigCache;
import kireiko.dev.millennium.ml.ClientML;
import kireiko.dev.millennium.types.EvictingList;
import lombok.Getter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class MX {

    public static final String
            command = "mx",
            name = "MX",
            permissionHead = "mx.",
            permission = permissionHead + "admin";
    public static int bannedPerMinuteCount = 0;
    public static List<Integer> bannedPerMinuteList = new EvictingList<>(60);
    public static int blockedPerMinuteCount = 0;
    public static List<Integer> blockedPerMinuteList = new EvictingList<>(60);
    @Getter
    private static MX instance;
    @Getter
    private static final Logger logger = Logger.getLogger("MX");
    @Getter
    private static File dataFolder = new File("plugins/MX");

    public static void initialize() {
        instance = new MX();
        instance.init();
    }

    private void init() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        logger.info("Initializing MX Anti-Cheat for Minestom...");
        CheckManager.init();
        ConfigCache.loadConfig();
        kireiko.dev.anticheat.managers.DatasetManager.init();

        logger.info("Loading listeners...");
        loadListeners();
        logger.info("Booting timers...");
        punishTimer();
        logger.info("Initializing commands...");
        MXCommandHandler handler = new MXCommandHandler();
        MinecraftServer.getCommandManager().register(handler);
        logger.info("Launching ML (Kireiko Millennium 5)...");
        ClientML.run();
        logger.info("Launched!\n"
                        + "        :::   :::       :::    :::\n" +
                        "      :+:+: :+:+:      :+:    :+:\n" +
                        "    +:+ +:+:+ +:+      +:+  +:+  \n" +
                        "   +#+  +:+  +#+       +#++:+\n" +
                        "  +#+       +#+      +#+  +#+\n" +
                        " #+#       #+#     #+#    #+#\n" +
                        "###       ###     ###    ###\n" +
                        "\nCreated by pawsashatoy (Kireiko Oleksandr)\n"
        );
    }

    private void punishTimer() {
        AnimatedPunishService.init();
        FunThingsService.init();
        SimulationFlagService.init();

        MinecraftServer.getSchedulerManager().buildTask(() -> {
            float r = ConfigCache.VL_RESET;
            bannedPerMinuteList.add(bannedPerMinuteCount);
            bannedPerMinuteCount = 0;
            blockedPerMinuteList.add(blockedPerMinuteCount);
            blockedPerMinuteCount = 0;
            for (PlayerProfile profile : PlayerContainer.getUuidPlayerProfileMap().values()) {
                profile.fade(r);
                profile.setFlagCount(0);
            }
        }).repeat(60, net.minestom.server.utils.time.TimeUnit.SECOND).schedule();
    }

    private void loadListeners() {
        GlobalEventHandler handler = MinecraftServer.getGlobalEventHandler();
        JoinQuitListener.register(handler);
        RawMovementListener.register(handler);
        UseEntityListener.register(handler);
        LatencyHandler.register(handler);
        VelocityListener.register(handler);
        EntityActionListener.register(handler);
        VehicleTeleportListener.register(handler);
        OmniPacketListener.register(handler);
    }

    public static void shutdown() {
        AsyncScheduler.shutdown();
    }
}
