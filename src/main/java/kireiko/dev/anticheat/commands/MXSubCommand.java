package kireiko.dev.anticheat.commands;

import kireiko.dev.anticheat.MX;
import lombok.Getter;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;

@Getter
public abstract class MXSubCommand {

    protected final String name;

    public MXSubCommand(String name) {
        this.name = name;
    }

    public abstract String getDescription();

    public String getUsage() {
        return "/" + MX.command + " " + getName();
    }

    public String getPermission() {
        return null;
    }

    public boolean hasPermission(CommandSender sender) {
        if (getPermission() == null || getPermission().isEmpty()) {
            return true;
        }
        return true;
    }

    public abstract int getMinArgs();

    public abstract int getMaxArgs();

    public abstract boolean onlyPlayerCanUse();

    public abstract boolean onCommand(CommandSender sender, String[] args);

    public abstract java.util.List<String> onTabComplete(CommandSender sender, String[] args);
}
