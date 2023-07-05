package me.scb.Commands;

import com.projectkorra.projectkorra.command.PKCommand;
import me.scb.Configuration.ConfigManager;
import me.scb.ProjectCoco;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand extends PKCommand {
    private final static Component COMPONENT_HELP = Component.text("ProjectCoco")
            .color(TextColor.color(255,140,172))
            .decorate(TextDecoration.BOLD)
            .append(Component.newline())
            .append(Component.text("Developed by ")
            .color(TextColor.color(255,140,172))
            .decorate(TextDecoration.BOLD))
            .append(Component.text(ProjectCoco.getAuthor() + " & collaborators")
                    .color(TextColor.color(213,93,127))
                    .decorate(TextDecoration.BOLD,TextDecoration.ITALIC))
            .append(Component.newline())
            .append(Component.text("ProjectCoco Version: ")
                    .color(TextColor.color(255,140,172))
                    .decorate(TextDecoration.BOLD))
            .append(Component.text(ProjectCoco.getVersion())
                    .color(TextColor.color(213,93,127))
                    .decorate(TextDecoration.BOLD,TextDecoration.ITALIC));

    private final static Component COMPONENT_RELOAD = Component.text("ProjectCoco")
            .color(TextColor.color(0,161,21))
            .decorate(TextDecoration.BOLD)
            .append(Component.text(" reloaded")
                    .color(TextColor.color(89,255,11))
                    .decorate(TextDecoration.BOLD,TextDecoration.ITALIC));


    public ReloadCommand() {
        super("projectcoco", "/b projectcoco", "Provides information about the ProjectCoco plugin", new String[]{"pc"});
    }

    @Override
    public void execute(CommandSender commandSender, List<String> args) {
        if (!hasPermission(commandSender) || !correctLength(commandSender, args.size(), 0, 1)) return;

        if (args.isEmpty()) {
            commandSender.sendMessage(COMPONENT_HELP);
        }else if (hasPermission(commandSender,"reload")){
            ProjectCoco.getPlugin().reloadConfig();
            ConfigManager.configPath.reloadConfig();
            ConfigManager.configPath.save();
            commandSender.sendMessage(COMPONENT_RELOAD);

        }
    }
}
