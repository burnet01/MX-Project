package kireiko.dev.anticheat.commands.subcommands;

import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.checks.aim.ml.AimMLCheck;
import kireiko.dev.anticheat.commands.MXSubCommand;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DatasetCommand extends MXSubCommand {

    public DatasetCommand() {
        super("dataset");
    }

    @Override
    public String getDescription() {
        return "Manage dataset recording for ML";
    }

    @Override
    public String getUsage() {
        return "/mx dataset <legit|cheat|off> <player|all>";
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public boolean onlyPlayerCanUse() {
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        String mode = args[0].toLowerCase();
        String targetArg = args[1].toLowerCase();

        if (!mode.equals("legit") && !mode.equals("cheat") && !mode.equals("off")) {
            sender.sendMessage("§cUse: legit, cheat, or off.");
            return true;
        }

        Boolean isCheat = null;
        if (mode.equals("cheat")) {
            isCheat = Boolean.TRUE;
        } else if (mode.equals("legit")) {
            isCheat = Boolean.FALSE;
        }

        if (targetArg.equals("all") || targetArg.equals("*")) {
            int count = 0;
            for (Player p : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                if (isCheat == null) {
                    AimMLCheck.RECORDING.remove(p.getUuid());
                } else {
                    AimMLCheck.RECORDING.put(p.getUuid(), isCheat);
                }
                count++;
            }
            sender.sendMessage("§aApplied dataset mode §e" + mode.toUpperCase() + " §ato §e" + count + " §aonline players.");
        } else {
            Player target = MinecraftServer.getConnectionManager().getOnlinePlayerByUsername(args[1]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found.");
                return true;
            }

            if (isCheat == null) {
                AimMLCheck.RECORDING.remove(target.getUuid());
                sender.sendMessage("§eStopped recording for " + target.getUsername());
            } else {
                AimMLCheck.RECORDING.put(target.getUuid(), isCheat);
                sender.sendMessage(isCheat ? "§cNow recording CHEAT data for " + target.getUsername() : "§aNow recording LEGIT data for " + target.getUsername());
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return Arrays.asList("legit", "cheat", "off");
        if (args.length == 2) {
            List<String> suggestions = new ArrayList<>();
            suggestions.add("all");
            for (Player p : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                suggestions.add(p.getUsername());
            }
            return suggestions;
        }
        return null;
    }
}
