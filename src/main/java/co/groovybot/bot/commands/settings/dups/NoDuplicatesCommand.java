package co.groovybot.bot.commands.settings.dups;

import co.groovybot.bot.core.command.*;
import co.groovybot.bot.core.command.permission.Permissions;

public class NoDuplicatesCommand extends SubCommand {

    NoDuplicatesCommand() {
        super(new String[]{"prevent", "no", "deny"}, Permissions.djMode(), "Toggles the option whether you want to queue duplicated songs or not.", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (event.getGroovyGuild().isPreventDups()) {
            event.getGroovyGuild().setPreventDups(false);
            return send(success(event.translate("command.preventdups.disabled.title"), event.translate("command.preventdups.disabled.description")));

        } else {
            event.getGroovyGuild().setPreventDups(true);
            return send(success(event.translate("command.preventdups.enabled.title"), event.translate("command.preventdups.enabled.description")));
        }
    }
}
