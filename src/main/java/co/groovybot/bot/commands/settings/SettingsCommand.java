package co.groovybot.bot.commands.settings;

import co.groovybot.bot.core.command.*;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;

public class SettingsCommand extends Command {

    public SettingsCommand() {
        super(new String[]{"settings", "set"}, CommandCategory.SETTINGS, Permissions.everyone(), "Shows you all current settings", "");
        registerSubCommand(new ResetCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Colors.DARK_BUT_NOT_BLACK);
        embedBuilder.setTitle("\uD83D\uDD27 " + event.translate("command.settings.title"));
        embedBuilder.setDescription(event.translate("command.settings.description"));
        embedBuilder.addField(String.format("**%s**", event.translate("command.settings.prefix")), String.format("**```%s```**", event.getGroovyGuild().getPrefix()), true);
        embedBuilder.addField(event.translate("command.settings.djmode"), String.format("**```%s```**", event.getGroovyGuild().isDjMode()), true);
        embedBuilder.addField(event.translate("command.settings.announce"), String.format("**```%s```**", event.getGroovyGuild().isAnnounceSongs()), true);
        embedBuilder.addField(event.translate("command.settings.leave"), String.format("**```%s```**", event.getGroovyGuild().isAutoLeave()), true);
        embedBuilder.addField(event.translate("command.settings.pause"), String.format("**```%s```**", event.getGroovyGuild().isAutoPause()), true);
        embedBuilder.addField(event.translate("command.settings.play"), String.format("**```%s```**", event.getBot().getMusicPlayerManager().getPlayer(event).getScheduler().isAutoPlay()), true);
        return send(embedBuilder);
    }

    private class ResetCommand extends SubCommand {

        public ResetCommand() {
            super(new String[]{"reset"}, Permissions.adminOnly(), "Lets you reset all settings", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            return null;
        }
    }
}
