package co.groovybot.bot.commands.settings;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
public class DeleteMessagesCommand extends Command {
    public DeleteMessagesCommand() {
        super(new String[]{"messages", "msgs"}, CommandCategory.SETTINGS, Permissions.adminOnly(), "Toggles that groovy is deleting his messages.");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (event.getGroovyGuild().isDeleteMessage()) {
            event.getGroovyGuild().setDeleteMessage(false);
            return send(success(event.translate("command.messages.disabled.title"), event.translate("command.messages.disabled.description")));
        }

        event.getGroovyGuild().setDeleteMessage(true);
        return send(success(event.translate("command.messages.enabled.title"), event.translate("command.messages.enabled.description")));
    }
}
