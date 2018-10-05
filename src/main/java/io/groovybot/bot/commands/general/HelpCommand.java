package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.*;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.entity.EntityProvider;
import io.groovybot.bot.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.Comparator;
import java.util.stream.Collectors;

import static io.groovybot.bot.util.FormatUtil.formatCommand;

public class HelpCommand extends Command {

    public HelpCommand() {
        super(new String[]{"help", "h", "?"}, CommandCategory.GENERAL, Permissions.everyone(), "Displays a list of all commands", "[command]");
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

    private EmbedBuilder formatCommandList(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(":information_source: " + event.translate("command.help.title"))
                .setColor(Colors.DARK_BUT_NOT_BLACK)
                .setDescription(String.format(event.translate("command.help.description"), EntityProvider.getGuild(event.getGuild().getIdLong()).getPrefix()));
        for (CommandCategory commandCategory : CommandCategory.class.getEnumConstants()) {
            String formattedCategory = fromatCategory(commandCategory, event.getGroovyBot().getCommandManager());
            if (!"".equals(formattedCategory))
                builder.addField(commandCategory.getDisplayName(), formattedCategory, false);
        }
        return builder;
    }

    private String fromatCategory(CommandCategory commandCategory, CommandManager manager) {
        StringBuilder stringBuilder = new StringBuilder();
        manager.getCommandAssociations().values().stream().distinct().sorted(Comparator.comparing(Command::getName)).filter(command -> command.getCommandCategory() == commandCategory).collect(Collectors.toList()).forEach(command -> stringBuilder.append("`").append(command.getName()).append("`, "));
        if (stringBuilder.toString().contains(","))
            stringBuilder.replace(stringBuilder.lastIndexOf(","), stringBuilder.lastIndexOf(",") + 1, "");
        return stringBuilder.toString();
    }
}
