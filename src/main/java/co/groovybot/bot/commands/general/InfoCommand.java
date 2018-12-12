package co.groovybot.bot.commands.general;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.entity.EntityProvider;

public class InfoCommand extends Command {
    public InfoCommand() {
        super(new String[]{"info", "i", "about"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you useful information about Groovy", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        String prefix = EntityProvider.getGuild(event.getGuild().getIdLong()).getPrefix();
        return send(
                info(event.translate("command.info.title"), String.format(event.translate("command.info.description"), prefix, prefix, prefix))
                        .addField(event.translate("command.info.invite"), "**[invite.groovybot.co](https://invite.groovybot.co)**", true)
                        .addField(event.translate("command.info.support"), "**[support.groovybot.co](https://discord.gg/5s5TsW2)**", true)
                        .addField(event.translate("command.info.donate"), "**[donate.groovybot.co](https://donate.groovybot.co)**", true)
                        .addField(event.translate("command.info.translate"), "**[i18n.groovybot.co](https://i18n.groovybot.co)**", true)
                        .addField(event.translate("command.info.sourcecode"), "**[github.com/GroovyDevs](https://github.com/GroovyDevs)**", true)
                        .addField(event.translate("command.info.sponsor"), "**[deinserverhost.de](https://deinserverhost.de/aff.php?aff=2892)**", true)
                        .addField(event.translate("command.info.youtube"), "**[youtube.com/groovy](https://www.youtube.com/channel/UCINfOUGimNIL-8A2BAG0jaw)**", true)
                        .addField(event.translate("command.info.twitter"), "**[twitter.com/groovydevs](https://twitter.com/groovydevs)**", true)
                        .addField(event.translate("command.info.twitch"), "**[twitch.tv/groovydevs](https://twitch.tv/groovydevs)**", true)
        );
    }
}
