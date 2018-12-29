package co.groovybot.bot.commands.settings;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SameChannelCommand;
import co.groovybot.bot.core.entity.Guild;

public class PlaySearchCommand extends SameChannelCommand {

    public PlaySearchCommand() {
        super(new String[]{"playsearch", "ps", "tooglesearch"}, CommandCategory.SETTINGS, Permissions.adminOnly(), "Toggle the selection when using the play command!", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        Guild guild = event.getGroovyGuild();
        guild.setSearchPlay(!guild.isSearchPlay());
        return send(success(event.translate("phrases.success"), String.format(event.translate("command.playsearch"), guild.isSearchPlay() ? event.translate("phrases.text.enabled") : event.translate("phrases.text.disabled"))));
    }
}
