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

package co.groovybot.bot.util;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.restaction.MessageAction;

import java.util.concurrent.TimeUnit;

@Log4j2
@SuppressWarnings("unused")
public class SafeMessage extends JDAUtil {

    private static MessageAction getAction(TextChannel channel, Message message) {
        if (hasWritePermissions(channel))
            if (hasEmbedPermissions(channel) || message.getEmbeds().isEmpty())
                return channel.sendMessage(message);
            else
                return channel.sendMessage(formatEmbed(message));
        return channel.getGuild().getOwner().getUser().openPrivateChannel().complete().sendMessage(EmbedUtil.error("No permissions!", String.format("I am unable to write in %s on your guild!", channel.getAsMention())).build());
    }

    private static MessageAction getEditAction(Message previousMessage, Message newMessage) {
        TextChannel channel = previousMessage.getTextChannel();
        if (hasWritePermissions(channel))
            if (hasEmbedPermissions(channel))
                return previousMessage.editMessage(newMessage);
            else
                return previousMessage.editMessage(formatEmbed(newMessage));
        return channel.getGuild().getOwner().getUser().openPrivateChannel().complete().sendMessage(EmbedUtil.error("No permissions!", String.format("I am unable to write in %s on your guild!", channel.getAsMention())).build());
    }

    public static void editMessage(Message previousMessage, EmbedBuilder builder) {
        if (previousMessage == null) return;
        getEditAction(previousMessage, buildMessage(builder)).queue();
    }

    public static void editMessage(Message previousMessage, String content) {
        if (previousMessage == null) return;
        getEditAction(previousMessage, buildMessage(content)).queue();
    }

    public static void sendMessage(TextChannel channel, Message message) {
        if (message == null) return;
        getAction(channel, message).queue();
    }

    public static void sendMessage(TextChannel channel, EmbedBuilder embedBuilder) {
        getAction(channel, buildMessage(embedBuilder)).queue();
    }

    public static void sendMessage(TextChannel channel, Message message, Integer delTime) {
        if (message == null) return;
        getAction(channel, message).queue(msg -> msg.delete().queueAfter(delTime, TimeUnit.SECONDS));
    }

    public static void sendMessage(TextChannel channel, EmbedBuilder embed, Integer delTime) {
        if (channel == null) return;
        getAction(channel, buildMessage(embed)).queue(msg -> msg.delete().queueAfter(delTime, TimeUnit.SECONDS));
    }

    public static Message sendMessageBlocking(TextChannel channel, Message message) {
        return waitForEntity(getAction(channel, message));
    }

    public static Message sendMessageBlocking(TextChannel channel, EmbedBuilder embedBuilder) {
        return waitForEntity(getAction(channel, buildMessage(embedBuilder)));
    }


    private static Message formatEmbed(Message message) {
        if (message.getEmbeds().isEmpty())
            return buildMessage(message.getContentRaw());
        else {
            MessageEmbed embed = message.getEmbeds().get(0);
            StringBuilder string = new StringBuilder();
            if (embed.getTitle() != null)
                string.append("**__").append(embed.getTitle()).append("__**").append("\n");
            if (embed.getDescription() != null)
                string.append(embed.getDescription());
            embed.getFields().forEach(field -> string.append("**__").append(field.getName()).append("__**\n").append(field.getValue()).append("\n"));
            if (embed.getFooter() != null)
                string.append("\n").append("_").append(embed.getFooter().getText()).append("_");
            String out = string.toString();
            if (string.length() > 1024)
                out = "This message is longer than 1024 chars, please give me `MESSAGE_EMBED_LINKS` permission and try again";
            return buildMessage(out);
        }
    }

    private static boolean hasWritePermissions(TextChannel channel) {
        if (channel == null) return false;
        return channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE);
    }

    private static boolean hasEmbedPermissions(TextChannel channel) {
        return channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_EMBED_LINKS);
    }

    private static Message buildMessage(MessageEmbed embed) {
        return new MessageBuilder().setEmbed(embed).build();
    }

    private static Message buildMessage(EmbedBuilder embedBuilder) {
        return buildMessage(embedBuilder.build());
    }

    private static Message buildMessage(String content) {
        return new MessageBuilder().setContent(content).build();
    }
}
