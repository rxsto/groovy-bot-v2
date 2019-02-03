/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergej Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package co.groovybot.bot.commands.settings;

import co.groovybot.bot.core.command.*;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.util.Colors;
import co.groovybot.bot.util.FormatUtil;
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
        embedBuilder.addField(String.format("%s", event.translate("command.settings.prefix")), String.format("`%s`", event.getGroovyGuild().getPrefix()), true);
        embedBuilder.addField(String.format("%s", event.translate("command.settings.djmode")), String.format("`%s`", event.getGroovyGuild().isDjMode()), true);
        embedBuilder.addField(String.format("%s", event.translate("command.settings.djrole")), String.format("%s", event.getGroovyGuild().getDjRole() != 0 ? FormatUtil.getRoleName(event.getGuild().getRoleById(event.getGroovyGuild().getDjRole())) : String.format("`%s`", event.translate("phrases.none"))), true);
        embedBuilder.addField(String.format("%s", event.translate("command.settings.announce")), String.format("`%s`", event.getGroovyGuild().isAnnounceSongs()), true);
        embedBuilder.addField(String.format("%s", event.translate("command.settings.join")), String.format("`%s`", event.getGroovyGuild().hasAutoJoinChannel()), true);
        embedBuilder.addField(String.format("%s", event.translate("command.settings.leave")), String.format("`%s`", event.getGroovyGuild().isAutoLeave()), true);
        embedBuilder.addField(String.format("%s", event.translate("command.settings.pause")), String.format("`%s`", event.getGroovyGuild().isAutoPause()), true);
        embedBuilder.addField(String.format("%s", event.translate("command.settings.play")), String.format("`%s`", event.getBot().getMusicPlayerManager().getPlayer(event).getScheduler().isAutoPlay()), true);
        embedBuilder.addField(String.format("%s", event.translate("command.settings.dupes")), String.format("`%s`", event.getGroovyGuild().isPreventDups()), true);
        embedBuilder.addField(String.format("%s", event.translate("command.settings.searchplay")), String.format("`%s`", event.getGroovyGuild().isSearchPlay()), true);
        embedBuilder.addField(String.format("%s", event.translate("command.settings.channelblacklist")), String.format("`%s`", event.getGroovyGuild().getBlacklistedChannels().size()), true);
        embedBuilder.addField(String.format("%s", event.translate("command.settings.botchannel")), String.format("%s", event.getGroovyGuild().getBotChannel() == 0 ? String.format("`%s`", event.translate("phrases.none")) : event.getBot().getShardManager().getTextChannelById(event.getGroovyGuild().getBotChannel()).getAsMention()), true);
        return send(embedBuilder);
    }

    private static class ResetCommand extends SubCommand {

        ResetCommand() {
            super(new String[]{"reset"}, Permissions.adminOnly(), "Lets you reset all settings", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            event.getGroovyGuild().reset();
            return send(success(event.translate("phrases.success"), event.translate("command.settings.reset")));
        }
    }
}
