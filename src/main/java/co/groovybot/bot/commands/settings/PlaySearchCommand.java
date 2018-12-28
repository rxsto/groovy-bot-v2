package co.groovybot.bot.commands.settings;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.entity.EntityProvider;
import co.groovybot.bot.core.entity.Guild;

public class PlaySearchCommand extends Command {

    public PlaySearchCommand() {
        super(new String[]{"playsearch", "ps", "tooglesearch"}, CommandCategory.SETTINGS, Permissions.adminOnly(), "Toggle the selection when using the play command!");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        Guild groovyGuild = EntityProvider.getGuild(event.getGuild().getIdLong());
        if(groovyGuild.isSearchPlay())
            groovyGuild.setSearchPlay(false);
        else
            groovyGuild.setSearchPlay(true);
        return send(success(event.translate("command.searchplay.title"), String.format(event.translate("command.searchplay.description"), groovyGuild.isSearchPlay())));
    }
}
