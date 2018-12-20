package co.groovybot.bot.commands.settings;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import net.dv8tion.jda.core.Permission;

public class AutoPauseCommand extends Command {

    public AutoPauseCommand() {
        super(new String[]{"autopause"}, CommandCategory.SETTINGS, Permissions.tierOne(), "If this option is enabled the bot will stop playing music while nobody is in the channel", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR))
            return send(error(event.translate("phrases.nopermission.title"), event.translate("phrases.nopermission.admin")));
        else {
            if (event.getGroovyGuild().isAutoPause()) {
                event.getGroovyGuild().setAutoPause(false);
                return send(success(event.translate("command.autopause.disabled.title"), event.translate("command.autopause.disabled.description")));
            } else {
                event.getGroovyGuild().setAutoPause(true);
                return send(success(event.translate("command.autopause.enabled.title"), event.translate("command.autopause.enabled.description")));
            }
        }
    }
}
