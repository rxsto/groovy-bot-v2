package co.groovybot.bot.commands.settings.dups;

import co.groovybot.bot.commands.music.dups.RemoveDuplicatesCommand;
import co.groovybot.bot.core.command.*;
import co.groovybot.bot.core.command.permission.Permissions;

public class DuplicatesCommand extends Command {

    public DuplicatesCommand() {
        super(new String[]{"duplicates", "duplicate", "dups"}, CommandCategory.SETTINGS, Permissions.djMode(), "Settings related to duplicates in the queue.", "");

        this.registerSubCommand(new NoDuplicatesCommand());
        this.registerSubCommand(new RemoveDuplicatesCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return sendHelp();
    }
}
