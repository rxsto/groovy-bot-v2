package io.groovybot.bot.util;

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
        return channel.getGuild().getOwner().getUser().openPrivateChannel().complete().sendMessage(String.format(":no_entry_sign: I am unable to write on your server in channel %s!", channel.getAsMention()));
    }

    private static MessageAction getEditAction(Message previousMessage, Message newMessage) {
        TextChannel channel = previousMessage.getTextChannel();
        if (hasWritePermissions(channel))
            if (hasEmbedPermissions(channel))
                return previousMessage.editMessage(newMessage);
            else
                return previousMessage.editMessage(formatEmbed(newMessage));
        return channel.getGuild().getOwner().getUser().openPrivateChannel().complete().sendMessage(String.format(":no_entry_sign: I am unable to write on your server in channel %s!", channel.getAsMention()));
    }

    public static void editMessage(Message prevoiusMessage, EmbedBuilder builder) {
        getEditAction(prevoiusMessage, buildMessage(builder)).queue();
    }

    public static void editMessage(Message prevoiusMessage, String content) {
        getEditAction(prevoiusMessage, buildMessage(content)).queue();
    }

    public static void sendMessage(TextChannel channel, Message message) {
        getAction(channel, message).queue();
    }

    public static void sendMessage(TextChannel channel, EmbedBuilder embedBuilder) {
        getAction(channel, buildMessage(embedBuilder)).queue();
    }

    public static void sendMessage(TextChannel channel, Message message, Integer delTime) {
        getAction(channel, message).queue(msg -> msg.delete().queueAfter(delTime, TimeUnit.SECONDS));
    }

    public static void sendMessage(TextChannel channel, EmbedBuilder embed, Integer delTime) {
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
