package co.groovybot.bot.commands.general;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;

public class InviteCommand extends Command {
    public InviteCommand() {
        super(new String[]{"invite", "inv"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you an invite for Groovy", "[-absolute]");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return send(noTitle(String.format("**[%s](https://discordapp.com/oauth2/authorize?client_id=%s&scope=bot&permissions=70610241)**", event.translate("command.invite"), event.getJDA().getSelfUser().getId())));
    }
}
