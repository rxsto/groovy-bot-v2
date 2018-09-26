package io.groovybot.bot.commands.general;

import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.entity.EntityProvider;
import io.groovybot.bot.util.Info;

public class InfoCommand extends Command {
    public InfoCommand() {
        super(new String[] {"info"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you some useful information", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        String prefix = EntityProvider.getGuild(event.getGuild().getIdLong()).getPrefix();
        return send(
                info(event.translate("command.info.title"), String.format(event.translate("command.info.description"), prefix, prefix, prefix))
                .addField(event.translate("command.info.developers"), "**Rxsto#4224\n Schlaubi#0001\n Wumpus#0117**", false)
                .addField(event.translate("command.info.version"), "`" + Info.VERSION + "`", false)
                .addField(event.translate("command.info.defaultprefix"), "`" + event.getGroovyBot().getConfig().getJSONObject("settings").getString("prefix") + " `", false)
                .addField(event.translate("command.info.customprefix"), "`" + prefix + "`", true)
                .addField(event.translate("command.info.shards"), "`" + String.valueOf(event.getGroovyBot().getShardManager().getShardsTotal()) + "`", false)
                .addField(event.translate("command.info.servers"), "`" + String.valueOf(event.getGroovyBot().getShardManager().getGuilds().size()) + "`", true)
                .addField(event.translate("command.info.sourcecode"), "[github.com/GroovyDevs](http://github.com/GroovyDevs)", false)
                .addField(event.translate("command.info.translate"), "[i18n.groovybot.space](http://i18n.groovybot.space)", true)
                .addField(event.translate("command.info.support"), "[discord.gg/5s5TsW2](https://discord.gg/5s5TsW2)", false)
                .addField(event.translate("command.info.invite"), "[groovybot.gq/invite](https://groovybot.gq/invite)", true)
        );
    }
}
