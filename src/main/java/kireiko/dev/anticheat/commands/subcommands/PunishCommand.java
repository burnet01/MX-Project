package kireiko.dev.anticheat.commands.subcommands;

import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.commands.MXSubCommand;
import net.minestom.server.command.CommandSender;

import java.util.List;

public final class PunishCommand extends MXSubCommand {

    public PunishCommand() {
        super("punish");
    }

    @Override
    public String getDescription() {
        return "Punish a player";
    }

    @Override
    public String getUsage() {
        return "/mx punish <player>";
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
            sender.sendMessage("§cPlayer not found... Sorry!");
            return true;
        }
        playerProfile.punish("Skill issue", "Bad guy", "Punish (Staff)", 999.0f);
        sender.sendMessage("§aPunished " + playerProfile.getPlayer().getName() + "!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return null;
        return List.of();
    }
}
