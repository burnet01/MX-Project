package kireiko.dev.anticheat.commands;

import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.commands.subcommands.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

import java.util.*;

public final class MXCommandHandler extends Command {

    private final Set<MXSubCommand> subCommands = new LinkedHashSet<>();

    public MXCommandHandler() {
        super("mx");
        this.subCommands.add(new AlertCommand());
        this.subCommands.add(new LogCommand());
        this.subCommands.add(new BroadcastCommand());
        this.subCommands.add(new DebugCommand());
        this.subCommands.add(new FunCommand());
        this.subCommands.add(new InfoCommand());
        this.subCommands.add(new PunishCommand());
        this.subCommands.add(new ReloadCommand());
        this.subCommands.add(new StatsCommand());
        this.subCommands.add(new ActivityCommand());
        this.subCommands.add(new MLCommand());
        this.subCommands.add(new DatasetCommand());
        this.subCommands.add(new TrainCommand());

        setDefaultExecutor((sender, context) -> {
            showHelps(sender);
        });

        var subCommandArg = ArgumentType.StringArray("subcommand");

        addSyntax((sender, context) -> {
            String[] parts = context.get(subCommandArg);
            if (parts.length == 0) {
                showHelps(sender);
                return;
            }
            String sCommand = parts[0];
            String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];
            for (MXSubCommand subCommand : subCommands) {
                if (!subCommand.getName().equalsIgnoreCase(sCommand)) continue;
                if (subCommand.onlyPlayerCanUse() && !(sender instanceof Player)) {
                    sender.sendMessage(Component.text("This command can only be used by player!", NamedTextColor.RED));
                    return;
                }
                if (!subCommand.hasPermission(sender)) {
                    sender.sendMessage(Component.text("You don't have permission!", NamedTextColor.RED));
                    return;
                }
                subCommand.onCommand(sender, args);
                return;
            }
            showHelps(sender);
        }, subCommandArg);
    }

    private void showHelps(CommandSender sender) {
        sender.sendMessage(Component.text(MX.name + " Commands", NamedTextColor.AQUA).decorate(TextDecoration.BOLD));
        sender.sendMessage(Component.empty());
        for (MXSubCommand subCommand : subCommands) {
            if (subCommand.onlyPlayerCanUse() && !(sender instanceof Player)) continue;
            if (!subCommand.hasPermission(sender)) continue;

            Component message = Component.text("/" + MX.command + " " + subCommand.getName() + " ", NamedTextColor.YELLOW)
                    .append(Component.text("- " + subCommand.getDescription(), NamedTextColor.RED));

            if (sender instanceof Player player) {
                message = message.clickEvent(ClickEvent.suggestCommand("/" + MX.command + " " + subCommand.getName()));
                message = message.hoverEvent(HoverEvent.showText(
                        Component.text("Command: ", NamedTextColor.YELLOW)
                                .append(Component.text(subCommand.getName(), NamedTextColor.RED))
                                .append(Component.newline())
                                .append(Component.text("Description: ", NamedTextColor.YELLOW))
                                .append(Component.text(subCommand.getDescription(), NamedTextColor.RED))
                                .append(Component.newline())
                                .append(Component.text("Usage: ", NamedTextColor.YELLOW))
                                .append(Component.text(subCommand.getUsage(), NamedTextColor.RED))
                ));
            }
            sender.sendMessage(message);
        }
    }
}
