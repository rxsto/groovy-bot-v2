package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.*;
import io.groovybot.bot.core.command.permission.Permissions;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class HelpCommand extends Command {

    public HelpCommand() {
        super(new String[] {"help", "h"}, CommandCategory.GENERAL, Permissions.everyone(), "Displays a list of all commands", "[command]");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (args.length == 0)
            return send(formatCommandList(event));
        if (!event.getGroovyBot().getCommandManager().getCommandAssociations().containsKey(args[0])) {
            return send(error(event.translate("command.help.notfound.title"), event.translate("command.help.notfound.description")));
        }
        Command command = event.getGroovyBot().getCommandManager().getCommandAssociations().get(args[0]);
        return send(formatCommand(command));
    }

    private EmbedBuilder formatCommand(Command command) {
        return info(command.getAliases()[0] + " - Help", formatUsage(command));
    }

    private String formatUsage(Command command) {
        StringBuilder stringBuilder = new StringBuilder();
        return addUsages(stringBuilder, command).toString();
    }

    private StringBuilder addUsages(StringBuilder stringBuilder, Command command) {
        stringBuilder.append("Command aliases: `").append(Arrays.toString(command.getAliases()).replace("[", "").replace("]", "")).append("`\n");
        stringBuilder.append("Description: `").append(command.getDescription()).append("`").append("\n");
        stringBuilder.append("Usage: `").append(buildUsage(command)).append("`");
        command.getSubCommandAssociations().values().parallelStream().distinct().collect(Collectors.toList()).forEach(subCommand -> stringBuilder.append(buildUsage(subCommand)).append(" - ").append(subCommand.getDescription()).append("\n"));
        return stringBuilder;
    }

    private String buildUsage(Command command) {
        return "g!" + command.getAliases()[0] + command.getUsage();
    }

    private EmbedBuilder formatCommandList(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(event.translate("command.help.title"))
                .setColor(Color.BLUE);
        for (CommandCategory commandCategory : CommandCategory.class.getEnumConstants()) {
            String formattedCategory = fromatCategory(commandCategory, event.getGroovyBot().getCommandManager());
            if (!formattedCategory.equals(""))
                builder.addField(commandCategory.getDisplayName(), formattedCategory, false);
        }
        return builder;
    }

    private String fromatCategory(CommandCategory commandCategory, CommandManager manager) {
        StringBuilder stringBuilder = new StringBuilder();
        manager.getCommandAssociations().values().parallelStream().distinct().filter(command -> command.getCommandCategory() == commandCategory).collect(Collectors.toList()).forEach(command -> stringBuilder.append("`").append(command.getAliases()[0]).append("`, "));
        if (stringBuilder.toString().contains(","))
            stringBuilder.replace(stringBuilder.lastIndexOf(","), stringBuilder.lastIndexOf(",") + 1, "");
        return stringBuilder.toString();
    }
}
