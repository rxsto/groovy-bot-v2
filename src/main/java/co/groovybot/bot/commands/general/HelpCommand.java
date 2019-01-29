/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergej Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package co.groovybot.bot.commands.general;

import co.groovybot.bot.core.command.*;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.entity.EntityProvider;
import co.groovybot.bot.util.Colors;
import co.groovybot.bot.util.FormatUtil;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.Comparator;
import java.util.stream.Collectors;

public class HelpCommand extends Command {

    public HelpCommand() {
        super(new String[]{"help", "h", "?"}, CommandCategory.GENERAL, Permissions.everyone(), "Displays a list of all commands", "[command]");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (args.length == 0)
            return send(formatCommandList(event));

        if (!event.getBot().getCommandManager().getCommandAssociations().containsKey(args[0]))
            return send(error(event.translate("command.help.notfound.title"), event.translate("command.help.notfound.description")));

        Command command = event.getBot().getCommandManager().getCommandAssociations().get(args[0]);
        return send(FormatUtil.formatCommand(command));
    }

    private EmbedBuilder formatCommandList(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(event.translate("command.help.title"), "https://groovybot.co")
                .setColor(Colors.DARK_BUT_NOT_BLACK)
                .setDescription(String.format(event.translate("command.help.description"), EntityProvider.getGuild(event.getGuild().getIdLong()).getPrefix()));
        for (CommandCategory commandCategory : CommandCategory.class.getEnumConstants()) {
            String formattedCategory = formatCategory(commandCategory, event.getBot().getCommandManager());
            if (!"".equals(formattedCategory))
                builder.addField(commandCategory.getDisplayName(), formattedCategory, false);
        }
        return builder;
    }

    private String formatCategory(CommandCategory commandCategory, CommandManager manager) {
        StringBuilder stringBuilder = new StringBuilder();
        manager.getCommandAssociations().values().stream().distinct().sorted(Comparator.comparing(Command::getName)).filter(command -> command.getCommandCategory() == commandCategory).collect(Collectors.toList()).forEach(command -> stringBuilder.append("`").append(command.getName()).append("`, "));
        if (stringBuilder.toString().contains(","))
            stringBuilder.replace(stringBuilder.lastIndexOf(","), stringBuilder.lastIndexOf(",") + 1, "");
        return stringBuilder.toString();
    }
}
