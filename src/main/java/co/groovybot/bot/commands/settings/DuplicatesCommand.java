package co.groovybot.bot.commands.settings;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.*;
import co.groovybot.bot.core.command.permission.Permissions;
import lombok.extern.log4j.Log4j2;

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

    public class NoDuplicatesCommand extends SubCommand {

        public NoDuplicatesCommand() {
            super(new String[]{"prevent", "no", "deny"}, Permissions.djMode(), "Toggles the option whether you want to queue duplicated songs or not.", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (event.getGroovyGuild().isPreventDups()) {
                event.getGroovyGuild().setPreventDups(false);
                return send(success(event.translate("command.preventdups.disabled.title"), event.translate("command.preventdups.disabled.description")));

            } else {
                event.getGroovyGuild().setPreventDups(true);
                return send(success(event.translate("command.preventdups.enabled.title"), event.translate("command.preventdups.enabled.description")));
            }
        }
    }

    public class RemoveDuplicatesCommand extends SubCommand {

        public RemoveDuplicatesCommand() {
            super(new String[]{"remove", "rm"}, Permissions.djMode(), "Removes all duplicates from the queue.", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (!event.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
                return send(error(event.translate("phrases.notinchannel.title"), event.translate("phrases.notinchannel.description")));
            if (!event.getGuild().getSelfMember().getVoiceState().getChannel().equals(event.getMember().getVoiceState().getChannel()))
                return send(error(event.translate("phrases.notsamechannel.title"), event.translate("phrases.notsamechannel.description")));
            MusicPlayer musicPlayer = GroovyBot.getInstance().getMusicPlayerManager().getPlayer(event.getGuild(), event.getChannel());
            int dups = musicPlayer.removeDups();
            return send(success(event.translate("command.dups.removed.title"), String.format(event.translate("command.dups.removed.description"), dups)));
        }
    }
}
