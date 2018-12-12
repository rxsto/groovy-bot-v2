package co.groovybot.bot.commands.settings;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.entity.EntityProvider;
import co.groovybot.bot.core.entity.Guild;

public class AnnounceCommand extends Command {

    public AnnounceCommand() {
        super(new String[]{"announce", "announcesongs"}, CommandCategory.SETTINGS, Permissions.adminOnly(), "Lets you toggle the announcement-mode", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        Guild guild = EntityProvider.getGuild(event.getGuild().getIdLong());
        if (guild.isAnnounceSongs()) {
            guild.setAnnounceSongs(false);
            return send(success(event.translate("command.announce.disabled.title"), event.translate("command.announce.disabled.description")));
        }
        guild.setAnnounceSongs(true);
        return send(success(event.translate("command.announce.enabled.title"), event.translate("command.announce.enabled.description")));
    }
}
