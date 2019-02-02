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

package co.groovybot.bot.listeners;

import co.groovybot.bot.core.events.command.CommandExecutedEvent;
import co.groovybot.bot.core.events.command.CommandFailEvent;
import co.groovybot.bot.core.events.command.NoPermissionEvent;
import co.groovybot.bot.util.Colors;
import co.groovybot.bot.util.EmbedUtil;
import co.groovybot.bot.util.SafeMessage;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

@Log4j2
public class CommandLogger {

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onCommandExecution(CommandExecutedEvent executedEvent) {
        log.debug(String.format("[Command] %s - %s#%s | %s (%s)", executedEvent.getCommand().getName(), executedEvent.getAuthor().getName(), executedEvent.getAuthor().getDiscriminator(), executedEvent.getGuild().getName(), executedEvent.getGuild().getIdLong()));
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onCommandFail(CommandFailEvent failEvent) {
        log.error(String.format("[Command] %s - %s#%s | %s (%s)", failEvent.getCommand().getName(), failEvent.getAuthor().getName(), failEvent.getAuthor().getDiscriminator(), failEvent.getGuild().getName(), failEvent.getGuild().getIdLong()), failEvent.getThrowable());
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("❌ " + failEvent.translate("phrases.error.internal"))
                .setDescription(String.format("We're sorry, but an internal error occured %n```%s```", failEvent.getThrowable().getClass().getCanonicalName() + ": " + failEvent.getThrowable().getMessage()))
                .setColor(Colors.RED);
        SafeMessage.sendMessage(failEvent.getChannel(), builder);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onPermissionViolations(NoPermissionEvent noPermissionEvent) {
        String permission = noPermissionEvent.getCommand().getPermissions().getIdentifier().toLowerCase().replaceAll(" ", "");
        EmbedBuilder builder = EmbedUtil.error(noPermissionEvent.translate("phrases.nopermission"), noPermissionEvent.translate(String.format("phrases.nopermission.%s", permission)));
        if (permission.startsWith("tier"))
            builder.setFooter(noPermissionEvent.translate("phrases.premium.footer"), null);
        SafeMessage.sendMessage(noPermissionEvent.getChannel(), builder);
    }
}
