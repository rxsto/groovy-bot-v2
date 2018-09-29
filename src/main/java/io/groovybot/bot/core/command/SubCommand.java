package io.groovybot.bot.core.command;

import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.util.FormatUtil;
import lombok.Getter;
import lombok.Setter;

public abstract class SubCommand extends Command {

    @Setter
    @Getter
    private Command mainCommand;

    public SubCommand(String[] aliases, Permissions permissions, String description, String usage) {
        super(aliases, null, permissions, description, usage);
    }

    @Override
    public Result sendHelp() {
        return send(FormatUtil.formatCommand(mainCommand));
    }
}
