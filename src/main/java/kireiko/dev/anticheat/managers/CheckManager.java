package kireiko.dev.anticheat.managers;

import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.api.PacketCheckHandler;
import kireiko.dev.anticheat.api.data.ConfigLabel;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.checks.aim.AimAnalysisCheck;
import kireiko.dev.anticheat.checks.aim.AimComplexCheck;
import kireiko.dev.anticheat.checks.aim.AimHeuristicCheck;
import kireiko.dev.anticheat.checks.aim.AimStatisticsCheck;
import kireiko.dev.anticheat.checks.aim.ml.AimMLCheck;
import kireiko.dev.anticheat.checks.clicks.AutoClickerCheck;
import kireiko.dev.anticheat.checks.movement.BaritoneCheck;
import kireiko.dev.anticheat.checks.movement.GhostBlockAbuseCheck;
import kireiko.dev.anticheat.checks.protocol.SprintCheck;
import kireiko.dev.anticheat.checks.velocity.VelocityCheck;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class CheckManager {
    @Getter
    private Set<Class<? extends PacketCheckHandler>> checks = new HashSet<>();
    @Getter
    private final Map<String, PacketCheckHandler> instances = new ConcurrentHashMap<>();

    static {
        checks.addAll(Arrays.asList(
                AimHeuristicCheck.class,
                AimComplexCheck.class,
                AimAnalysisCheck.class,
                AimStatisticsCheck.class,
                AimMLCheck.class,
                VelocityCheck.class,
                AutoClickerCheck.class,
                BaritoneCheck.class,
                GhostBlockAbuseCheck.class,
                SprintCheck.class
        ));
    }

    @SneakyThrows
    public void init() {
        instances.clear();
        File file = new File(MX.getDataFolder(), "checks.yml");
        Map<String, Object> configData = new HashMap<>();

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    int colonIndex = line.indexOf(':');
                    if (colonIndex > 0) {
                        String key = line.substring(0, colonIndex).trim();
                        String value = line.substring(colonIndex + 1).trim();
                        configData.put(key, value);
                    }
                }
            }
        }

        for (Class<? extends PacketCheckHandler> handlerClass : checks) {
            PacketCheckHandler check = handlerClass
                    .getConstructor(PlayerProfile.class)
                    .newInstance((Object) null);
            ConfigLabel defaultLabel = check.config();

            String sectionName = defaultLabel.getName();
            Map<String, Object> defaultParams = defaultLabel.getParameters();

            Map<String, Object> mergedParams = new HashMap<>(defaultParams);
            for (Map.Entry<String, Object> e : defaultParams.entrySet()) {
                String key = e.getKey();
                if (configData.containsKey(sectionName + "." + key)) {
                    mergedParams.put(key, configData.get(sectionName + "." + key));
                }
            }

            check.applyConfig(mergedParams);
            instances.put(check.getClass().getName(), check);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Map.Entry<String, PacketCheckHandler> entry : instances.entrySet()) {
                PacketCheckHandler check = entry.getValue();
                ConfigLabel label = check.config();
                writer.write(label.getName() + ":");
                writer.newLine();
                for (Map.Entry<String, Object> param : label.getParameters().entrySet()) {
                    writer.write("  " + param.getKey() + ": " + param.getValue());
                    writer.newLine();
                }
            }
        }
    }

    public boolean classCheck(Class<?> clazz) {
        return (CheckManager.getInstances().containsKey(clazz.getName()));
    }

    public Map<String, Object> getConfig(Class<?> clazz) {
        return (CheckManager.getInstances().get(clazz.getName())).getConfig();
    }
}
