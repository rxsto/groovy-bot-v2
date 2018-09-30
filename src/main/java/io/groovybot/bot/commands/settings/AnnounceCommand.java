package io.groovybot.bot.commands.settings;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.entity.EntityProvider;
import io.groovybot.bot.core.entity.Guild;

public class AnnounceCommand extends Command {

    public AnnounceCommand() {
        super(new String[]{"announce", "announcesongs"}, CommandCategory.SETTINGS, Permissions.adminOnly(), "Lets you disable the song announcements", "");
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
