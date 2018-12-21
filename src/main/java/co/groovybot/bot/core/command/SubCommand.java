package co.groovybot.bot.core.command;

import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.util.FormatUtil;
import lombok.Getter;
import lombok.Setter;

public abstract class SubCommand extends Command {

    @Setter
    @Getter
    private Command mainCommand;

    public SubCommand(String[] aliases, Permissions permissions, String description, String usage) {
        super(aliases, null, permissions, description, usage);
    }

    public SubCommand(String[] aliases, Permissions permissions, String description) {
        super(aliases, null, permissions, description, "");
    }

    @Override
    public Result sendHelp() {
        return send(FormatUtil.formatCommand(mainCommand));
    }
}
