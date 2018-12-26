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

package co.groovybot.bot.core.command;

import co.groovybot.bot.util.SafeMessage;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

@RequiredArgsConstructor
public class Result {

    private final Message message;

    public Result(MessageBuilder messageBuilder) {
        this(messageBuilder.build());
    }

    public Result(MessageEmbed messageEmbed) {
        this(new MessageBuilder().setEmbed(messageEmbed));
    }

    public Result(String message) {
        this(new MessageBuilder().setContent(message));
    }

    public Result(EmbedBuilder embedBuilder) {
        this(embedBuilder.build());
    }

    public void sendMessage(TextChannel channel) {
        SafeMessage.sendMessage(channel, message);
    }

    public void sendMessage(TextChannel channel, Integer delTime) {
        SafeMessage.sendMessage(channel, message, delTime);
    }
}
