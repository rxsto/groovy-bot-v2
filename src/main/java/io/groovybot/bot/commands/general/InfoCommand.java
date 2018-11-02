package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.entity.EntityProvider;

public class InfoCommand extends Command {
    public InfoCommand() {
        super(new String[]{"info", "i", "about"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you useful information about Groovy", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        String prefix = EntityProvider.getGuild(event.getGuild().getIdLong()).getPrefix();
        return send(
                info(event.translate("command.info.title"), String.format(event.translate("command.info.description"), prefix, prefix, prefix))
                        .addField(event.translate("command.info.invite"), "**[groovybot.gq/invite](https://groovybot.gq/invite)**", true)
                        .addField(event.translate("command.info.support"), "**[discord.gg/5s5TsW2](https://discord.gg/5s5TsW2)**", true)
                        .addField(event.translate("command.info.donate"), "**[patreon.com/rxsto](https://groovybot.gq/donate)**", true)
                        .addField(event.translate("command.info.translate"), "**[i18n.groovybot.gq](http://i18n.groovybot.gq)**", true)
                        .addField(event.translate("command.info.sourcecode"), "**[github.com/GroovyDevs](http://github.com/GroovyDevs)**", true)
                        .addField(event.translate("command.info.sponsor"), "**[deinserverhost.de](https://deinserverhost.de/aff.php?aff=2892)**", true)
        );
    }
}
