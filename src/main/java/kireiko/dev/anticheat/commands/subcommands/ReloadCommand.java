package kireiko.dev.anticheat.commands.subcommands;

import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.commands.MXSubCommand;
import kireiko.dev.anticheat.managers.CheckManager;
import kireiko.dev.anticheat.utils.ConfigCache;
import net.minestom.server.command.CommandSender;

import java.util.List;

import static kireiko.dev.anticheat.utils.MessageUtils.wrapColors;

public final class ReloadCommand extends MXSubCommand {
    public ReloadCommand() {
        super("reload");
    }

    @Override
    public String getDescription() {
        return "Reload config";
    }

    @Override
    public int getMinArgs() {
        return 0;
    }

    @Override
    public int getMaxArgs() {
        return 0;
    }

    @Override
    public boolean onlyPlayerCanUse() {
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        ConfigCache.loadConfig();
        CheckManager.init();
        sender.sendMessage(wrapColors("&cConfig reloaded!"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
