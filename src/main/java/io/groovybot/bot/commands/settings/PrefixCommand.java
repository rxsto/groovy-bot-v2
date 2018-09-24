package io.groovybot.bot.commands.settings;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.entity.EntityProvider;
import io.groovybot.bot.core.entity.Guild;

public class PrefixCommand extends Command {

    public PrefixCommand() {
        super(new String[] {"prefix"}, CommandCategory.SETTINGS, Permissions.adminOnly(), "Sets the server's prefix", "[prefix]");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        Guild guild = EntityProvider.getGuild(event.getGuild().getIdLong());
        if (args.length == 0)
            return send(info(event.translate("command.prefix.current.title"), String.format(event.translate("command.prefix.current.description"), guild.getPrefix())));
        guild.setPrefix(args[0]);
        return send(success(event.translate("command.prefix.set.title"), String.format(event.translate("command.prefix.set.description"), args[0])));
    }
}
