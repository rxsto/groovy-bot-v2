package io.groovybot.bot.commands.settings;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;

public class UserSettingsCommand extends Command {

    public UserSettingsCommand() {
        super(new String[]{"usersettings", "userset", "user"}, CommandCategory.SETTINGS, Permissions.everyone(), "Shows you your settings", "[setting]");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return null;
    }
}
