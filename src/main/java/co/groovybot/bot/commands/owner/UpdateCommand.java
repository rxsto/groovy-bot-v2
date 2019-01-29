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

package co.groovybot.bot.commands.owner;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.util.EmbedUtil;
import co.groovybot.bot.util.SafeMessage;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

@Log4j2
public class UpdateCommand extends Command {

    public UpdateCommand() {
        super(new String[]{"update"}, CommandCategory.DEVELOPER, Permissions.ownerOnly(), "Lets you announce an update for Groovy", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        Message confirmMessage = SafeMessage.sendMessageBlocking(event.getChannel(), EmbedUtil.small(event.translate("command.update.confirmation")));
        confirmMessage.addReaction("✅").queue();
        confirmMessage.addReaction("❌").queue();
        event.getBot().getEventWaiter().waitForEvent(GuildMessageReactionAddEvent.class, e -> confirmMessage.getIdLong() == e.getMessageIdLong() && e.getGuild().equals(event.getGuild()) && !e.getUser().isBot(),
                e -> {

                    if (e.getReactionEmote().getName().equals("✅")) {
                        event.getBot().getMusicPlayerManager().updateAllPlayers();
                        SafeMessage.sendMessageBlocking(event.getChannel(), success(event.translate("phrases.success"), event.translate("command.update")));
                        confirmMessage.delete().queue();
                    } else {
                        SafeMessage.editMessage(confirmMessage, small(event.translate("command.update.cancel")));
                    }

                    confirmMessage.clearReactions().queue();
                });
        return null;
    }
}
