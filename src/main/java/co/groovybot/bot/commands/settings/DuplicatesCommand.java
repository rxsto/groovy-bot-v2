package co.groovybot.bot.commands.settings;

import co.groovybot.bot.core.command.*;
import co.groovybot.bot.core.command.permission.Permissions;

public class DuplicatesCommand extends Command {

    public DuplicatesCommand() {
        super(new String[]{"duplicates", "duplicate", "dups"}, CommandCategory.SETTINGS, Permissions.djMode(), "Settings related to duplicates in the queue.", "");
        this.registerSubCommand(new NoDuplicatesCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return sendHelp();
    }

    public class NoDuplicatesCommand extends SubCommand {

        public NoDuplicatesCommand() {
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

}
