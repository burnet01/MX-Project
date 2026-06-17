package kireiko.dev.anticheat.commands.subcommands;

import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.commands.MXSubCommand;
import kireiko.dev.millennium.math.Simplification;
import kireiko.dev.millennium.math.Statistics;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;

import java.util.List;

import static kireiko.dev.anticheat.utils.MessageUtils.wrapColors;

public final class InfoCommand extends MXSubCommand {
    public InfoCommand() {
        super("info");
    }

    @Override
    public String getDescription() {
        return "Get info about a player";
    }

    @Override
    public String getUsage() {
        return "/mx info <player>";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public boolean onlyPlayerCanUse() {
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerContainer.getProfileByName(args[0]);
        if (playerProfile == null) {
            sender.sendMessage(Component.text("Player not found... Sorry!", NamedTextColor.RED));
            return true;
        }
        String sens = wrapColors("&4Not enough info!");
        final int calculated = playerProfile.calculateSensitivity();
        if (calculated > -1) {
            sens = NamedTextColor.BLUE.toString() + calculated;
        }
        StringBuilder pingLabel = new StringBuilder();
        String delimiter = "";
        for (long ping : playerProfile.getPing()) {
            NamedTextColor color = getColorForPing(ping);
            pingLabel.append(delimiter).append(color.toString()).append(ping);
            if (delimiter.isEmpty()) {
                delimiter = NamedTextColor.WHITE + ", ";
            }
        }
        final String[] info = new String[]{
                "",
                wrapColors("&fInfo about &c" + playerProfile.getPlayer().getName()),
                "",
                wrapColors("&fPing (ms): " + pingLabel),
                wrapColors("&fJitter (ms): &9" + Simplification.scaleVal(Statistics.getStandardDeviation(playerProfile.getPing()), 2)),
                wrapColors("&fSensitivity: " + sens),
                wrapColors("&fVL: &c" + playerProfile.getVl()),
                ""
        };
        for (String i : info) {
            sender.sendMessage(i);
        }
        return true;
    }

    private NamedTextColor getColorForPing(long ping) {
        if (ping > 1000) return NamedTextColor.DARK_RED;
        if (ping > 300) return NamedTextColor.RED;
        if (ping > 100) return NamedTextColor.YELLOW;
        return NamedTextColor.GREEN;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return null;
        return List.of();
    }
}
