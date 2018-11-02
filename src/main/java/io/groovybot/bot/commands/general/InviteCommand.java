package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;

public class InviteCommand extends Command {
    public InviteCommand() {
        super(new String[]{"invite", "inv"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you an invite for Groovy", "[-absolute]");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (event.getMessage().getContentRaw().contains("-absolute"))
            return send(info("Absolute Invite-Link", String.format("https://discordapp.com/oauth2/authorize?client_id=%s&scope=bot&permissions=70610241", event.getJDA().getSelfUser().getId())));
        return send(info(event.translate("command.invite.title"), event.translate("command.invite.description")));
    }
}
