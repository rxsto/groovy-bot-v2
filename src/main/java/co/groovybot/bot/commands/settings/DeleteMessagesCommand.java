package co.groovybot.bot.commands.settings;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.entity.entities.GroovyGuild;

public class DeleteMessagesCommand extends Command {
    public DeleteMessagesCommand() {
        super(new String[]{"deletemessages", "messages", "delete", "msg"}, CommandCategory.SETTINGS, Permissions.adminOnly(), "Lets you toggle Groovy's behaviour to delete messages");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        GroovyGuild groovyGuild = event.getGroovyGuild();
        groovyGuild.setDeleteMessages(!groovyGuild.isDeleteMessages());
        return send(success(event.translate("phrases.success"), String.format(event.translate("command.deletemessages"), groovyGuild.isDeleteMessages() ? event.translate("phrases.text.enabled") : event.translate("phrases.text.disabled"))));
    }
}
